package com.awesometodo.service;

import com.awesometodo.dto.JwtAuthTokensDTO;
import com.awesometodo.dto.LoginDataDTO;
import com.awesometodo.dto.SignupDataDTO;
import com.awesometodo.dto.UserIdentityDTO;
import com.awesometodo.entity.PendingSignupUser;
import com.awesometodo.entity.User;
import com.awesometodo.exception.InvalidCredentialsException;
import com.awesometodo.exception.UserWithSameDetailsAlreadyExistsException;
import com.awesometodo.repository.PendingSignupUserRepository;
import com.awesometodo.repository.UserRepository;
import io.jsonwebtoken.Jwt;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private UserRepository userRepository;
    private PendingSignupUserRepository pendingSignupUserRepository;
    private Argon2PasswordEncoder argon2IdPasswordEncoder;
    private JwtService jwtService;
    private JwtRefreshTokenService jwtRefreshTokenService;

    public UserService(UserRepository userRepository, PendingSignupUserRepository pendingSignupUserRepository,Argon2PasswordEncoder argon2IdPasswordEncoder, JwtService jwtService, JwtRefreshTokenService jwtRefreshTokenService) {
        this.userRepository=userRepository;
        this.pendingSignupUserRepository=pendingSignupUserRepository;
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

    public void signupInitialization(SignupDataDTO signupDataDTO) {
        String receivedUserNameLC=signupDataDTO.getUserName().toLowerCase();
        String receivedEmailLC=signupDataDTO.getEmail().toLowerCase();
        String receivedPhoneNo=signupDataDTO.getPhoneNumber();

        boolean isUserWithSameDetailsAlreadyExists=userRepository.isExistsByUsernameOrEmailOrPhoneNo(new UserIdentityDTO(receivedUserNameLC, receivedEmailLC,receivedPhoneNo));

        if(isUserWithSameDetailsAlreadyExists)
            throw new UserWithSameDetailsAlreadyExistsException();

        /* .try to find rows within the pending_signup_users table that have same details as the received data: ->If no such rows(0 rows) are found then we can freely insert a new row into pending_signup_users table and generate otps for it and store them in signup_otps table.
        ->If rows are found then check the otp expiry column for each of them within the signup_otps table, if otp's for all of them are expired then we can delete all the rows and then follow the same steps as shown by -> first arrow step.  If atleast  one of the pending users does not have expired otps then we will have to throw PendingSignupUserWithSameDetailsAlreadyExistsException, this is because they have been reserved a username,email or phoneno for a total of 5 minutes until otp expires and till that time no one else should be able to use what they have used.
         */
        List<PendingSignupUser> pendingSignupUsers=pendingSignupUserRepository.findByUsernameOrEmailOrPhoneNo(new UserIdentityDTO(receivedUserNameLC,receivedEmailLC,receivedPhoneNo));

        boolean isNoPendingSignUserPresentWithSameDetails=pendingSignupUsers.isEmpty();

        if(isNoPendingSignUserPresentWithSameDetails) {

        }
        else {

        }


    }
}
