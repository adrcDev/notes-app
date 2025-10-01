package com.awesometodo.service;

import com.awesometodo.dto.*;
import com.awesometodo.entity.User;
import com.awesometodo.exception.InvalidCredentialsException;
import com.awesometodo.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.util.Optional;

@Service
public class UserLoginService {
    private final static Logger logger= LoggerFactory.getLogger(UserLoginService.class);
    private UserRepository userRepository;
    private Argon2PasswordEncoder argon2IdPasswordEncoder;
    private JwtService jwtService;
    private JwtRefreshTokenService jwtRefreshTokenService;


    public UserLoginService(UserRepository userRepository, Argon2PasswordEncoder argon2IdPasswordEncoder, JwtService jwtService, JwtRefreshTokenService jwtRefreshTokenService) {
        this.userRepository=userRepository;
        this.argon2IdPasswordEncoder=argon2IdPasswordEncoder;
        this.jwtService=jwtService;
        this.jwtRefreshTokenService=jwtRefreshTokenService;
    }

    @Transactional
    public JwtAuthTokensDTO login(LoginDataDTO loginDataDTO) {
        String userNameOrEmail=loginDataDTO.getUserNameOrEmail().toLowerCase();
        String password=loginDataDTO.getPassword();
        String exceptionMessage="Invalid username or password!";
        boolean isEmail=userNameOrEmail.contains("@");
        Optional<User> optional;
        String email="";
        String userName="";
        if(isEmail) {
            email=userNameOrEmail;
            logger.debug("Login is being attempted by email:{}",email);
            logger.debug("Trying to find an existing user account with email:{}",email);
            optional=userRepository.findByEmail(email);
        }
        else {
            userName=userNameOrEmail;
            logger.debug("Login is being attempted by username:{}",userName);
            logger.debug("Trying to find an existing user account with userName:{}",userName);
            optional=userRepository.findByUserName(userName);
        }

        boolean isUserAccountExists=optional.isPresent();
        if(!isUserAccountExists) {
            String logMessage="Login attempt was unsuccessful as the received {}:{} could not be found in the database";
            if(isEmail) {
                logger.warn(logMessage,"email",email);
            }
            else {
                logger.warn(logMessage,"username",userName);
            }
            throw new InvalidCredentialsException(exceptionMessage);
        }


        User userAccount=optional.get();
        logger.debug("A user account with id:{}, username:{} and email:{} was found in the database",userAccount.getId(),userAccount.getUserName(),userAccount.getEmail());

        if(isReceivedPasswordCorrectForUserAccount(userAccount,password)) {
            int userId=userAccount.getId();
            String jwtAccessToken=jwtService.generateJwtAccessToken(userId);
            logger.debug("jwt access token generated for user with id:{}, username:{} and email: {}",userId,userAccount.getUserName(),userAccount.getEmail());

            String jwtRefreshToken=jwtService.generateJwtRefreshToken(userId);
            logger.debug("jwt refresh token generated for user with id:{}, username:{} and email: {}",userId,userAccount.getUserName(),userAccount.getEmail());

            jwtRefreshTokenService.storeJwtRefreshToken(jwtRefreshToken);
            logger.debug("jwt refresh token was stored in database for user with id:{}, username:{} and email: {}",userId,userAccount.getUserName(),userAccount.getEmail());
            logger.info("Login attempt was successful for user account with id:{}, username:{} and email:{} as the received password matched the stored password in the database",userAccount.getId(),userAccount.getUserName(),userAccount.getEmail());
            return new JwtAuthTokensDTO(jwtAccessToken,jwtRefreshToken);
        }
        else {
            logger.warn("Login failed for user account with id:{}, username:{} and email:{} as the received password did not match the stored password in the database",userAccount.getId(),userAccount.getUserName(),userAccount.getEmail());
            throw new InvalidCredentialsException(exceptionMessage);
        }


    }

    private boolean isReceivedPasswordCorrectForUserAccount(User userAccount,String receivedPassword) {
        String passwordHash=userAccount.getPasswordHash();
        String normalizedReceivedPassword=Normalizer.normalize(receivedPassword,Normalizer.Form.NFC);
        boolean isReceivedPasswordCorrect=argon2IdPasswordEncoder.matches(normalizedReceivedPassword,passwordHash);
        return isReceivedPasswordCorrect;
    }




}
