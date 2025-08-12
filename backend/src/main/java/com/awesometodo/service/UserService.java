package com.awesometodo.service;

import com.awesometodo.dto.JwtAuthTokensDTO;
import com.awesometodo.dto.LoginDataDTO;
import com.awesometodo.entity.User;
import com.awesometodo.exception.InvalidCredentialsException;
import com.awesometodo.repository.UserRepository;
import io.jsonwebtoken.Jwt;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.util.Optional;

@Service
public class UserService {
    private UserRepository userRepository;
    private Argon2PasswordEncoder argon2IdPasswordEncoder;
    private JwtService jwtService;
    private JwtRefreshTokenService jwtRefreshTokenService;

    public UserService(UserRepository userRepository,Argon2PasswordEncoder argon2IdPasswordEncoder,JwtService jwtService,JwtRefreshTokenService jwtRefreshTokenService) {
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
        if(isEmail) {
            String email=userNameOrEmail;
            optional=userRepository.findByEmail(email);
        }
        else {
            String userName=userNameOrEmail;
            optional=userRepository.findByUserName(userName);
        }

        if(optional.isPresent()) {
            User userAccount=optional.get();
            if(isReceivedPasswordCorrectForUserAccount(userAccount,password)) {
                int userId=userAccount.getId();
                String jwtAccessToken=jwtService.generateJwtAccessToken(userId);
                String jwtRefreshToken=jwtService.generateJwtRefreshToken(userId);
                jwtRefreshTokenService.storeJwtRefreshToken(jwtRefreshToken);
                return new JwtAuthTokensDTO(jwtAccessToken,jwtRefreshToken);
            }
            else {
                throw new InvalidCredentialsException(exceptionMessage);
            }
        }
        else {
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
