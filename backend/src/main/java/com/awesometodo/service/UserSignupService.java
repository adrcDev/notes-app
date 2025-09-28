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
        logger.debug("Signup otp's verification process initiated with received username:{}, phone number otp and email otp",usernameLC);
        logger.debug("Trying to find a pending sign user with the received username:{}",usernameLC);
        Optional<Integer> optional=pendingSignupUserRepository.findIdByUsername(usernameLC);
        boolean isPendingSignupUserDoesntExist=optional.isEmpty();
        if(isPendingSignupUserDoesntExist) {
            logger.warn("No pending signup user exists who has the received username:{}",usernameLC);
            throw new PendingSignupUserDoesntExistException("No pending signup up user exists who has the received username");
        }

        logger.debug("A pending signup user exists with received username:{}",usernameLC);
        int pendingSignupUserId=optional.get();
        boolean isOtpsForPendingSignupUserExpired=signupOtpRepository.isOtpsForPendingSignupUserIdExpired(pendingSignupUserId).get();
        if(isOtpsForPendingSignupUserExpired) {
            logger.warn("The generated and stored signup phone number and email otp's for the received username:{} have already expired",usernameLC);
            throw new SignupOtpsExpiredException("Both the phone number and email otp's for the pending sign up user have expired");
        }

        logger.debug("The generated and stored signup phone number and email otp's for the received username:{} are still active and not expired",usernameLC);
        String phoneNumberOtp=signupOtpRepository.findPhoneNumberOtpByPendingSignupUserId(pendingSignupUserId).get();
        String emailOtp=signupOtpRepository.findEmailOtpByPendingSignupUserId(pendingSignupUserId).get();
        String receivedPhoneNoOtp=signupOtpVerificationDataDTO.getPhoneNumberOtp();
        String receivedEmailOtp=signupOtpVerificationDataDTO.getEmailOtp();
        boolean isReceivedPhoneNoOtpCorrect=receivedPhoneNoOtp.equals(phoneNumberOtp);
        if(!isReceivedPhoneNoOtpCorrect) {
            logger.warn("The received phone number signup otp for the received username:{} does not match the generated and stored phone number signup otp",usernameLC);
            throw new OtpMismatchException("received phone number otp did not match expected otp value");
        }

        logger.debug("The received phone number signup otp for the received username:{} matches the generated and stored phone number signup otp",usernameLC);
        boolean isReceivedEmailOtpCorrect=receivedEmailOtp.equals(emailOtp);
        if(!isReceivedEmailOtpCorrect) {
            logger.warn("The received email signup otp for the received username:{} does not match the generated and stored email signup otp",usernameLC);
            throw new OtpMismatchException("received email otp did not match expected otp value");
        }

        logger.debug("The received email signup otp for the received username:{} matches the generated and stored email signup otp",usernameLC);
        PendingSignupUser pendingSignupUser=pendingSignupUserRepository.findById(pendingSignupUserId).get();
        try {
            logger.debug("Trying to create a user account for the pending signup user with username:{}",usernameLC);
            createUserFromPendingSignupUser(pendingSignupUser);
            logger.info("User account successfully created for pending signup user with username:{},email:{}",usernameLC,pendingSignupUser.getEmail());
        } catch(DataIntegrityViolationException e) {
            logger.warn("User account creation for pending signup with username:{},email:{} failed due to concurrent insertion",usernameLC,pendingSignupUser.getEmail());
            throw new UserWithSameDetailsAlreadyExistsException();
        }
        logger.debug("Trying to delete pending signup user with username:{},email:{} as corresponding user account has been created",usernameLC,pendingSignupUser.getEmail());
        pendingSignupUserRepository.delete(pendingSignupUser);
        logger.info("Deleted pending signup user with username:{},email:{} as corresponding user account has been created",usernameLC,pendingSignupUser.getEmail());

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
