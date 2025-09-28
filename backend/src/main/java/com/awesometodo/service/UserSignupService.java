package com.awesometodo.service;

import com.awesometodo.dto.SignupDataDTO;
import com.awesometodo.dto.SignupOtpVerificationDataDTO;
import com.awesometodo.entity.PendingSignupUser;
import com.awesometodo.entity.User;
import com.awesometodo.exception.*;
import com.awesometodo.repository.PendingSignupUserRepository;
import com.awesometodo.repository.SignupOtpRepository;
import com.awesometodo.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.CannotSerializeTransactionException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserSignupService {
    private static final Logger logger= LoggerFactory.getLogger(UserSignupService.class);

    PendingSignupUserRepository pendingSignupUserRepository;
    SignupOtpRepository signupOtpRepository;
    UserRepository userRepository;
    OtpService otpService;

    public UserSignupService(PendingSignupUserRepository pendingSignupUserRepository,SignupOtpRepository signupOtpRepository,OtpService otpService,UserRepository userRepository) {
        this.pendingSignupUserRepository=pendingSignupUserRepository;
        this.signupOtpRepository=signupOtpRepository;
        this.otpService=otpService;
        this.userRepository=userRepository;
    }


    @Retryable(maxAttempts = 5,backoff = @Backoff(300L),retryFor = {PessimisticLockingFailureException.class},recover = "signupVerifyOtpsRecoveryMethod")
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void signupVerifyOtps(SignupOtpVerificationDataDTO signupOtpVerificationDataDTO) {
        String usernameLC=signupOtpVerificationDataDTO.getUsername().toLowerCase();
        Optional<Integer> optional=pendingSignupUserRepository.findIdByUsername(usernameLC);
        boolean isPendingSignupUserDoesntExist=optional.isEmpty();
        if(isPendingSignupUserDoesntExist) {
            throw new PendingSignupUserDoesntExistException("No pending signup up user exists who has the received username");
        }

        int pendingSignupUserId=optional.get();
        boolean isOtpsForPendingSignupUserExpired=signupOtpRepository.isOtpsForPendingSignupUserIdExpired(pendingSignupUserId).get();
        if(isOtpsForPendingSignupUserExpired) {
            throw new SignupOtpsExpiredException("Both the phone number and email otp's for the pending sign up user have expired");
        }

        String phoneNumberOtp=signupOtpRepository.findPhoneNumberOtpByPendingSignupUserId(pendingSignupUserId).get();
        String emailOtp=signupOtpRepository.findEmailOtpByPendingSignupUserId(pendingSignupUserId).get();
        String receivedPhoneNoOtp=signupOtpVerificationDataDTO.getPhoneNumberOtp();
        String receivedEmailOtp=signupOtpVerificationDataDTO.getEmailOtp();
        boolean isReceivedPhoneNoOtpCorrect=receivedPhoneNoOtp.equals(phoneNumberOtp);
        if(!isReceivedPhoneNoOtpCorrect) {
            throw new OtpMismatchException("received phone number otp did not match expected otp value");
        }
        boolean isReceivedEmailOtpCorrect=receivedEmailOtp.equals(emailOtp);
        if(!isReceivedEmailOtpCorrect) {
            throw new OtpMismatchException("received email otp did not match expected otp value");
        }

        PendingSignupUser pendingSignupUser=pendingSignupUserRepository.findById(pendingSignupUserId).get();
        try {
            createUserFromPendingSignupUser(pendingSignupUser);
        } catch(DataIntegrityViolationException e) {
            throw new UserWithSameDetailsAlreadyExistsException();
        }
        pendingSignupUserRepository.delete(pendingSignupUser);

    }

    @Recover
    private void signupVerifyOtpsRecoveryMethod(PessimisticLockingFailureException e,SignupOtpVerificationDataDTO signupOtpVerificationDataDTO) {
        logger.error("The signupVerifyOtps method was retried multiple times but still a serialization anomaly kept being detected by the database. The object passed as argument to the method:{}",signupOtpVerificationDataDTO);
        throw e;
    }

    private void createUserFromPendingSignupUser(PendingSignupUser pendingSignupUser) {
        User userToBeCreated=new User(pendingSignupUser.getUserName(), pendingSignupUser.getDisplayName(), pendingSignupUser.getEmail(), pendingSignupUser.getPasswordHash(), pendingSignupUser.getDateOfBirth(),pendingSignupUser.getGender(),pendingSignupUser.getPhoneNo());
        userRepository.insert(userToBeCreated);
    }



}
