package com.awesometodo.service;

import com.awesometodo.dto.SignupDataDTO;
import com.awesometodo.dto.SignupOtpVerificationDataDTO;
import com.awesometodo.dto.UserIdentityDTO;
import com.awesometodo.entity.PendingSignupUser;
import com.awesometodo.entity.SignupOtp;
import com.awesometodo.entity.User;
import com.awesometodo.entity.enums.Gender;
import com.awesometodo.exception.*;
import com.awesometodo.repository.PendingSignupUserRepository;
import com.awesometodo.repository.SignupOtpRepository;
import com.awesometodo.repository.UserRepository;
import com.awesometodo.util.EnumUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.CannotSerializeTransactionException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class UserSignupService {
    private static final Logger logger= LoggerFactory.getLogger(UserSignupService.class);

    private PendingSignupUserRepository pendingSignupUserRepository;
    private SignupOtpRepository signupOtpRepository;
    private UserRepository userRepository;
    private OtpService otpService;
    private Argon2PasswordEncoder argon2IdPasswordEncoder;

    public UserSignupService(PendingSignupUserRepository pendingSignupUserRepository,SignupOtpRepository signupOtpRepository,OtpService otpService,UserRepository userRepository,Argon2PasswordEncoder argon2IdPasswordEncoder) {
        this.pendingSignupUserRepository=pendingSignupUserRepository;
        this.signupOtpRepository=signupOtpRepository;
        this.otpService=otpService;
        this.userRepository=userRepository;
        this.argon2IdPasswordEncoder=argon2IdPasswordEncoder;
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

    @Transactional
    public void signupResendOtps(SignupDataDTO signupDataDTO) {
        String receivedUsernameLC=signupDataDTO.getUserName().toLowerCase();
        String receivedEmailLC=signupDataDTO.getEmail().toLowerCase();
        boolean isUserWithSameDetailsAlreadyExists=userRepository.isExistsByUsernameOrEmailOrPhoneNo(new UserIdentityDTO(receivedUsernameLC,receivedEmailLC,signupDataDTO.getPhoneNumber()));

        if(isUserWithSameDetailsAlreadyExists)
            throw new UserWithSameDetailsAlreadyExistsException();

        Optional<PendingSignupUser> optional=findExactMatchingPendingSignupUser(signupDataDTO);
        boolean isExactMatchPendingSignupUserExists=optional.isPresent();
        if(isExactMatchPendingSignupUserExists) {
            PendingSignupUser pendingSignupUser=optional.get();
            Optional<Boolean> optionalBoolean=signupOtpRepository.isOtpsForPendingSignupUserIdExpired(pendingSignupUser.getId());
            boolean isSignupOtpsExpired=optionalBoolean.get();
            if(isSignupOtpsExpired) {
                updateSignupOtpsForPendingSignupUser(pendingSignupUser);
                return;
            }
            throw new SignupOtpsNotExpiredException();
        }

        List<PendingSignupUser> pendingSignupUserList=pendingSignupUserRepository.findByUsernameOrEmailOrPhoneNo(new UserIdentityDTO(receivedUsernameLC,receivedEmailLC,signupDataDTO.getPhoneNumber()));

        boolean isPendingSignUsersWithSameDetailsNotExists= pendingSignupUserList.isEmpty();
        if(isPendingSignUsersWithSameDetailsNotExists) {
            PendingSignupUser createdPendingSignupUser;
            try {
                createdPendingSignupUser = createAndReturnPendingSignupUser(signupDataDTO);
            } catch(DataIntegrityViolationException e) {
                throw new PendingSignupUserWithSameDetailsAlreadyExistsException();
            }
            createSignupOtpsForCreatedPendingSignupUser(createdPendingSignupUser);
            return;
        }

        if(!isOtpsForAllPendingSignupUsersExpired(pendingSignupUserList)) {
            throw new PendingSignupUserWithSameDetailsAlreadyExistsException();
        }

        try {
            deletePendingSignupUsersWhoseOtpsWereExpired(pendingSignupUserList);
        } catch(InvalidDataAccessApiUsageException e) {
            throw new PendingSignupUserWithSameDetailsAlreadyExistsException();
        }

        PendingSignupUser createdPendingSignupUser=createAndReturnPendingSignupUser(signupDataDTO);
        createSignupOtpsForCreatedPendingSignupUser(createdPendingSignupUser);
    }

    private void updateSignupOtpsForPendingSignupUser(PendingSignupUser pendingSignupUser) {
        String newPhoneNumberOtp=otpService.sendOtpToPhoneNo(pendingSignupUser.getPhoneNo());
        String newEmailOtp=otpService.sendOtpToEmail(pendingSignupUser.getEmail().toLowerCase());
        int rowsUpdated=signupOtpRepository.updatePhoneNumberOtpByPendingSignupUserId(newPhoneNumberOtp,pendingSignupUser.getId());
        rowsUpdated=signupOtpRepository.updateEmailOtpByPendingSignupUserId(newEmailOtp,pendingSignupUser.getId());
    }

    private Optional<PendingSignupUser> findExactMatchingPendingSignupUser(SignupDataDTO signupDataDTO) {
        Optional<PendingSignupUser> optional=pendingSignupUserRepository.findBySignUpData(signupDataDTO);
        if(optional.isEmpty())
            return Optional.empty();

        PendingSignupUser pendingSignupUser=optional.get();
        String receivedPassword=signupDataDTO.getPassword();
        String unicodeNormalizedPassword= Normalizer.normalize(receivedPassword, Normalizer.Form.NFC);
        boolean isPendingSignupUserWithExactMatchingDetailsExists=argon2IdPasswordEncoder.matches(unicodeNormalizedPassword,pendingSignupUser.getPasswordHash());
        if(isPendingSignupUserWithExactMatchingDetailsExists)
            return Optional.of(pendingSignupUser);

        return Optional.empty();
    }

    private PendingSignupUser createAndReturnPendingSignupUser(SignupDataDTO signupDataDTO) {
        String displayName= signupDataDTO.getUserName();
        String receivedUserNameLC= signupDataDTO.getUserName().toLowerCase();
        String receivedEmailLC=signupDataDTO.getEmail().toLowerCase();
        LocalDate dateOfBirthAsLocalDate=LocalDate.parse(signupDataDTO.getDateOfBirth());
        Gender genderAsGenderEnumConstant= EnumUtil.convertStringToSpecifiedEnumClassConstant(signupDataDTO.getGender(), Gender.class).get();
        String unicodeNormalizedPassword=Normalizer.normalize(signupDataDTO.getPassword(), Normalizer.Form.NFC);
        String passwordHash=argon2IdPasswordEncoder.encode(unicodeNormalizedPassword);
        PendingSignupUser toBeCreatedPendingSignupUser=
                new PendingSignupUser(receivedUserNameLC,displayName,receivedEmailLC, signupDataDTO.getPhoneNumber(),dateOfBirthAsLocalDate, genderAsGenderEnumConstant,passwordHash);
        PendingSignupUser createdPendingSignupUser=
                pendingSignupUserRepository.insertAndReturn(toBeCreatedPendingSignupUser);
        return createdPendingSignupUser;
    }

    private void createSignupOtpsForCreatedPendingSignupUser(PendingSignupUser createdPendingSignupUser) {
        String emailOtp=otpService.sendOtpToEmail(createdPendingSignupUser.getEmail());
        String phoneNoOtp=otpService.sendOtpToPhoneNo(createdPendingSignupUser.getPhoneNo());

        SignupOtp emailSignupOtp=new SignupOtp(emailOtp, SignupOtp.OtpType.EMAIL,createdPendingSignupUser);
        SignupOtp phoneSignupOtp=new SignupOtp(phoneNoOtp, SignupOtp.OtpType.PHONE,createdPendingSignupUser);
        signupOtpRepository.insert(emailSignupOtp);
        signupOtpRepository.insert(phoneSignupOtp);

    }

    private boolean isOtpsForAllPendingSignupUsersExpired(List<PendingSignupUser> pendingSignupUsers) {
        for(PendingSignupUser pendingSignupUser : pendingSignupUsers) {
            if(!signupOtpRepository.isOtpsForPendingSignupUserIdExpired(pendingSignupUser.getId()).get())
                return false;
        }

        return true;
    }

    private void deletePendingSignupUsersWhoseOtpsWereExpired(List<PendingSignupUser> pendingSignupUsers) {
        for(PendingSignupUser pendingSignupUser : pendingSignupUsers) {
            pendingSignupUserRepository.delete(pendingSignupUser);
        }
    }





}
