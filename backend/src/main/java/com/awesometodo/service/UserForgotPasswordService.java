package com.awesometodo.service;

import com.awesometodo.dto.ForgotPassswordDataDTO;
import com.awesometodo.entity.ForgotPasswordOtp;
import com.awesometodo.entity.User;
import com.awesometodo.entity.enums.OtpType;
import com.awesometodo.exception.ConcurrentOperationException;
import com.awesometodo.exception.UserDoesntExistException;
import com.awesometodo.repository.ForgotPasswordOtpRepository;
import com.awesometodo.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserForgotPasswordService {
    private static final Logger logger= LoggerFactory.getLogger(UserForgotPasswordService.class);
    private UserRepository userRepository;
    private ForgotPasswordOtpRepository forgotPasswordOtpRepository;
    private OtpService otpService;

    public UserForgotPasswordService(UserRepository userRepository,ForgotPasswordOtpRepository forgotPasswordOtpRepository,OtpService otpService) {
        this.userRepository=userRepository;
        this.forgotPasswordOtpRepository=forgotPasswordOtpRepository;
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
        List<ForgotPasswordOtp> forgotPasswordOtps =forgotPasswordOtpRepository.findByUserId(user.getId());
        boolean isForgotPasswordOtpsExistsForUserId=forgotPasswordOtps.size()==2;
        if(isForgotPasswordOtpsExistsForUserId) {
            logger.debug("Forgot-password-otp's exist for the user account with username:{} and email:{}.Trying to delete both the existing forgot-password-otp's",user.getUserName(),user.getEmail());
            try {
                forgotPasswordOtpRepository.delete(forgotPasswordOtps.get(0));
                forgotPasswordOtpRepository.delete(forgotPasswordOtps.get(1));
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




}
