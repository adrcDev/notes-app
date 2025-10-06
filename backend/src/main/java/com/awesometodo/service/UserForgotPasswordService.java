package com.awesometodo.service;

import com.awesometodo.dto.ForgotPassswordDataDTO;
import com.awesometodo.entity.ForgotPasswordOtp;
import com.awesometodo.entity.User;
import com.awesometodo.entity.enums.OtpType;
import com.awesometodo.exception.ConcurrentOperationException;
import com.awesometodo.exception.UserDoesntExistException;
import com.awesometodo.repository.ForgotPasswordOtpRepository;
import com.awesometodo.repository.UserRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Optional;

@Service
public class UserForgotPasswordService {
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
        Optional<User> optional=userRepository.findByUsernameAndEmail(receivedUsernameLC,receivedEmailLC);
        boolean isUserDoesntExist=optional.isEmpty();
        if(isUserDoesntExist) {
            throw new UserDoesntExistException();
        }

        User user=optional.get();
        List<ForgotPasswordOtp> forgotPasswordOtps =forgotPasswordOtpRepository.findByUserId(user.getId());
        boolean isForgotPasswordOtpsExistsForUserId=forgotPasswordOtps.size()==2;
        if(isForgotPasswordOtpsExistsForUserId) {
            try {
                forgotPasswordOtpRepository.delete(forgotPasswordOtps.get(0));
                forgotPasswordOtpRepository.delete(forgotPasswordOtps.get(1));
            } catch(InvalidDataAccessApiUsageException e) {
                String exceptionMessage="Forgot password  initialisation process attempt failed for user with username:"+receivedUsernameLC+" and email:"+receivedEmailLC+". The same user concurrently tried to start the forgot password initialisation process so the deletion of a otp failed in one of the threads as it had already been deleted by another thread";
                throw new ConcurrentOperationException(exceptionMessage,e);
            }
        }

        try {
            createForgotPasswordOtpsForUser(user);
        } catch(DataIntegrityViolationException e) {
            String exceptionMessage="Forgot password initialisation process attempt failed for user with username:"+receivedUsernameLC+" and email:"+receivedEmailLC+". The same user concurrently tried to start the forgot password initialisation process so the insertion of a otp failed in one of the threads as it had already been inserted before by another thread leading to violation of compositie unique constraint on user_id(FK) and type columns";
            throw new ConcurrentOperationException(exceptionMessage,e);
        }
    }

    private void createForgotPasswordOtpsForUser(User user) {
        String userEmailLC=user.getEmail().toLowerCase();
        String emailOtp=otpService.sendOtpToEmail(userEmailLC);
        String phoneNoOtp=otpService.sendOtpToPhoneNo(user.getPhoneNo());
        ForgotPasswordOtp emailForgotPasswordOtp=new ForgotPasswordOtp(emailOtp,user, OtpType.EMAIL);
        ForgotPasswordOtp phoneNoForgotPasswordOtp=new ForgotPasswordOtp(phoneNoOtp,user, OtpType.PHONE);
        forgotPasswordOtpRepository.insert(emailForgotPasswordOtp);
        forgotPasswordOtpRepository.insert(phoneNoForgotPasswordOtp);
    }




}
