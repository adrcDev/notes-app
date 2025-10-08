package com.awesometodo.service;

import com.awesometodo.dto.ForgotPassswordDataDTO;
import com.awesometodo.dto.ForgotPasswordOtpVerificationDataDTO;
import com.awesometodo.entity.ForgotPasswordOtp;
import com.awesometodo.entity.PasswordResetToken;
import com.awesometodo.entity.User;
import com.awesometodo.entity.enums.OtpType;
import com.awesometodo.exception.*;
import com.awesometodo.repository.ForgotPasswordOtpRepository;
import com.awesometodo.repository.PasswordResetTokenRepository;
import com.awesometodo.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserForgotPasswordService {
    private static final Logger logger= LoggerFactory.getLogger(UserForgotPasswordService.class);
    private UserRepository userRepository;
    private ForgotPasswordOtpRepository forgotPasswordOtpRepository;
    private PasswordResetTokenRepository passwordResetTokenRepository;
    private OtpService otpService;

    public UserForgotPasswordService(UserRepository userRepository,ForgotPasswordOtpRepository forgotPasswordOtpRepository,PasswordResetTokenRepository passwordResetTokenRepository,OtpService otpService) {
        this.userRepository=userRepository;
        this.forgotPasswordOtpRepository=forgotPasswordOtpRepository;
        this.passwordResetTokenRepository=passwordResetTokenRepository;
        this.otpService=otpService;
    }

    @Transactional
    public void forgotPasswordInitialisation(ForgotPassswordDataDTO forgotPassswordDataDTO) {
        String receivedUsernameLC=forgotPassswordDataDTO.getUsername().toLowerCase();
        String receivedEmailLC=forgotPassswordDataDTO.getEmail().toLowerCase();
        logger.debug("Forgot password initialisation process started with received username:{} and email:{}",receivedUsernameLC,receivedEmailLC);
        logger.debug("Checking to see if an user account exists whose username and email exactly match the received username:{} and email:{}",receivedUsernameLC,receivedEmailLC);
        Optional<User> optional=userRepository.findByUsernameAndEmail(receivedUsernameLC,receivedEmailLC);
        boolean isUserDoesntExist=optional.isEmpty();
        if(isUserDoesntExist) {
            logger.warn("No user account exists whose username and email exactly match the received username:{} and email:{}",receivedUsernameLC,receivedEmailLC);
            throw new UserDoesntExistException();
        }

        logger.debug("An user account was found whose username and email exactly match the received username:{} and email:{}",receivedUsernameLC,receivedEmailLC);
        User user=optional.get();
        logger.debug("Checking if forgot-password-otp's already exist for the user account with username:{} and email:{}",user.getUserName(),user.getEmail());
        List<ForgotPasswordOtp> forgotPasswordOtps =forgotPasswordOtpRepository.findByUserIdAndOrderByTypeASC(user.getId());
        boolean isForgotPasswordOtpsExistsForUserId=forgotPasswordOtps.size()==2;
        if(isForgotPasswordOtpsExistsForUserId) {
            logger.debug("Forgot-password-otp's exist for the user account with username:{} and email:{}.Trying to delete both the existing forgot-password-otp's",user.getUserName(),user.getEmail());
            try {
                ForgotPasswordOtp storedEmailForgotPasswordOtp=forgotPasswordOtps.get(0);
                ForgotPasswordOtp storedPhoneNoForgotPasswordOtp=forgotPasswordOtps.get(1);
                forgotPasswordOtpRepository.delete(storedEmailForgotPasswordOtp);
                logger.debug("Deleted the stored email forgot password otp for user with username:{} and email:{}",user.getUserName(),user.getEmail());
                forgotPasswordOtpRepository.delete(storedPhoneNoForgotPasswordOtp);
                logger.debug("Deleted the stored phone number forgot password otp for user with username:{} and email:{}",user.getUserName(),user.getEmail());
                logger.debug("Both forgot-password-otp's were deleted for the user account with username:{} and email:{}",user.getUserName(),user.getEmail());
            } catch(InvalidDataAccessApiUsageException e) {
                String exceptionMessage="Forgot password initialisation process attempt failed for user with username:"+user.getUserName()+" and email:"+user.getEmail()+". The same user concurrently tried to start the forgot password initialisation process so the deletion of a otp failed in one of the threads as it had already been deleted by another thread";
                throw new ConcurrentOperationException(exceptionMessage,e);
            }
        }
        else {
            logger.debug("Forgot-password-otp's don't exist for the user account with username:{} and email:{}",user.getUserName(),user.getEmail());
        }

        logger.debug("Trying to create new forgot-password-otp's for the user account with username:{} and email:{}",user.getUserName(),user.getEmail());
        try {
            createForgotPasswordOtpsForUser(user);
            logger.info("Forgot-password-otp's were created,stored and sent for the user account with username:{} and email:{} ",user.getUserName(),user.getEmail());
        } catch(DataIntegrityViolationException e) {
            String exceptionMessage="Forgot password initialisation process attempt failed for user with username:"+user.getUserName()+" and email:"+user.getEmail()+". The same user concurrently tried to start the forgot password initialisation process so the insertion of a otp failed in one of the threads as it had already been inserted before by another thread leading to violation of composite unique constraint on user_id(FK) and type columns";
            throw new ConcurrentOperationException(exceptionMessage,e);
        }
    }

    private void createForgotPasswordOtpsForUser(User user) {
        String emailOtp=otpService.sendOtpToEmail(user.getEmail());
        logger.debug("Generated and sent forgot-password-otp to email associated to user account with username:{} and email:{}",user.getUserName(),user.getEmail());
        String phoneNoOtp=otpService.sendOtpToPhoneNo(user.getPhoneNo());
        logger.debug("Generated and sent forgot-password-otp to the phone number associated to user account with username:{} and email:{}",user.getUserName(),user.getEmail());
        ForgotPasswordOtp emailForgotPasswordOtp=new ForgotPasswordOtp(emailOtp,user, OtpType.EMAIL);
        ForgotPasswordOtp phoneNoForgotPasswordOtp=new ForgotPasswordOtp(phoneNoOtp,user, OtpType.PHONE);
        logger.debug("Attempting to store the email and phone number forgot-password-otp's for the user account with username:{} and email:{}",user.getUserName(),user.getEmail());
        forgotPasswordOtpRepository.insert(emailForgotPasswordOtp);
        forgotPasswordOtpRepository.insert(phoneNoForgotPasswordOtp);
    }

    @Retryable(maxAttempts = 5,backoff = @Backoff(300L),retryFor = {PessimisticLockingFailureException.class},recover = "forgotPasswordVerifyOtpsRecoveryMethod")
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public String forgotPasswordVerifyOtps(ForgotPasswordOtpVerificationDataDTO forgotPasswordOtpVerificationDataDTO) {
        String receivedUsernameLC=forgotPasswordOtpVerificationDataDTO.getUsername().toLowerCase();
        String receivedEmailLC=forgotPasswordOtpVerificationDataDTO.getEmail().toLowerCase();

        Optional<User> optional=userRepository.findByUsernameAndEmail(receivedUsernameLC,receivedEmailLC);
        boolean userAccountDoesntExist=optional.isEmpty();
        if(userAccountDoesntExist) {
            throw new UserDoesntExistException();
        }

        User user=optional.get();
        List<ForgotPasswordOtp> forgotPasswordOtpList=forgotPasswordOtpRepository.findByUserIdAndOrderByTypeASC(user.getId());

        boolean isUserDoesntHaveForgotPasswordOtps=forgotPasswordOtpList.isEmpty();
        if(isUserDoesntHaveForgotPasswordOtps) {
            throw new UserDoesntHaveForgotPasswordOtpsException();
        }

        Optional<Boolean> optionalBoolean=forgotPasswordOtpRepository.isForgotPasswordOtpsExpiredForUserId(user.getId());
        boolean isForgotPasswordOtpsExpiredForUser=optionalBoolean.get();
        if(isForgotPasswordOtpsExpiredForUser) {
            throw new ForgotPasswordOtpsExpiredException();
        }

        String receivedEmailOtp=forgotPasswordOtpVerificationDataDTO.getEmailOtp();
        String storedEmailOtp=forgotPasswordOtpList.get(0).getOtp();
        if(!receivedEmailOtp.equals(storedEmailOtp)) {
            throw new ForgotPasswordOtpMismatchException("The received email forgot password otp did not match the stored email forgot password otp");
        }

        String receivedPhoneNoOtp= forgotPasswordOtpVerificationDataDTO.getPhoneNumberOtp();
        String storedPhoneNoOtp=forgotPasswordOtpList.get(1).getOtp();
        if(!receivedPhoneNoOtp.equals(storedPhoneNoOtp)) {
            throw new ForgotPasswordOtpMismatchException("The received phone number forgot password otp did not match the stored phone number forgot password otp");
        }

        ForgotPasswordOtp storedEmailOtpObj=forgotPasswordOtpList.get(0);
        ForgotPasswordOtp storedPhoneNoOtpObj=forgotPasswordOtpList.get(1);
        try {
            forgotPasswordOtpRepository.delete(storedEmailOtpObj);
        } catch(InvalidDataAccessApiUsageException e) {
            String exceptionMessage="The same user with username:"+user.getUserName()+" and email:"+user.getEmail()+" tried to concurrently delete their forgot password email otp due to which one of the deletions threw an exception";
            throw new ConcurrentOperationException(exceptionMessage,e);
        }
        forgotPasswordOtpRepository.delete(storedPhoneNoOtpObj);

        String passwordResetToken=createPasswordResetTokenForUser(user);
        return passwordResetToken;
    }

    @Recover
    private String forgotPasswordVerifyOtpsRecoveryMethod(PessimisticLockingFailureException e,ForgotPasswordOtpVerificationDataDTO forgotPasswordOtpVerificationDataDTO) {
        logger.error("The forgotPasswordVerifyOtps method was retried multiple times but still a serialization anomaly kept being detected by the database. The object passed as argument to the method:{}",forgotPasswordOtpVerificationDataDTO);
        throw e;
    }

    private String createPasswordResetTokenForUser(User user) {
        Optional<PasswordResetToken> optionalPasswordResetToken=passwordResetTokenRepository.findByUserId(user.getId());
        boolean isUserHasExistingPasswordResetToken=optionalPasswordResetToken.isPresent();
        if(isUserHasExistingPasswordResetToken) {
            PasswordResetToken passwordResetTokenToBeDeleted=optionalPasswordResetToken.get();
            passwordResetTokenRepository.delete(passwordResetTokenToBeDeleted);
        }

        PasswordResetToken newPasswordResetToken=new PasswordResetToken(user);
        passwordResetTokenRepository.insertAndRefresh(newPasswordResetToken);
        String passwordResetToken=newPasswordResetToken.getToken().toString();
        return passwordResetToken;
    }


}
