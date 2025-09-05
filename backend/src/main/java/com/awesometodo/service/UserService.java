package com.awesometodo.service;

import com.awesometodo.dto.JwtAuthTokensDTO;
import com.awesometodo.dto.LoginDataDTO;
import com.awesometodo.dto.SignupDataDTO;
import com.awesometodo.dto.UserIdentityDTO;
import com.awesometodo.entity.PendingSignupUser;
import com.awesometodo.entity.SignupOtp;
import com.awesometodo.entity.User;
import com.awesometodo.entity.enums.Gender;
import com.awesometodo.exception.InvalidCredentialsException;
import com.awesometodo.exception.PendingSignupUserWithSameDetailsAlreadyExistsException;
import com.awesometodo.exception.UserWithSameDetailsAlreadyExistsException;
import com.awesometodo.repository.PendingSignupUserRepository;
import com.awesometodo.repository.SignupOtpRepository;
import com.awesometodo.repository.UserRepository;
import com.awesometodo.util.EnumUtil;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private UserRepository userRepository;
    private PendingSignupUserRepository pendingSignupUserRepository;
    private SignupOtpRepository signupOtpRepository;
    private Argon2PasswordEncoder argon2IdPasswordEncoder;
    private JwtService jwtService;
    private JwtRefreshTokenService jwtRefreshTokenService;
    private OtpService otpService;


    public UserService(UserRepository userRepository, PendingSignupUserRepository pendingSignupUserRepository,Argon2PasswordEncoder argon2IdPasswordEncoder, JwtService jwtService, JwtRefreshTokenService jwtRefreshTokenService,OtpService otpService,SignupOtpRepository signupOtpRepository) {
        this.userRepository=userRepository;
        this.pendingSignupUserRepository=pendingSignupUserRepository;
        this.argon2IdPasswordEncoder=argon2IdPasswordEncoder;
        this.jwtService=jwtService;
        this.jwtRefreshTokenService=jwtRefreshTokenService;
        this.otpService=otpService;
        this.signupOtpRepository=signupOtpRepository;
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

        boolean isUserAccountExists=optional.isPresent();
        if(!isUserAccountExists)
            throw new InvalidCredentialsException(exceptionMessage);


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

    private boolean isReceivedPasswordCorrectForUserAccount(User userAccount,String receivedPassword) {
        String passwordHash=userAccount.getPasswordHash();
        String normalizedReceivedPassword=Normalizer.normalize(receivedPassword,Normalizer.Form.NFC);
        boolean isReceivedPasswordCorrect=argon2IdPasswordEncoder.matches(normalizedReceivedPassword,passwordHash);
        return isReceivedPasswordCorrect;
    }

    @Transactional
    public void signupInitialization(SignupDataDTO signupDataDTO) {
        String receivedUserNameLC=signupDataDTO.getUserName().toLowerCase();
        String receivedEmailLC=signupDataDTO.getEmail().toLowerCase();
        String receivedPhoneNo=signupDataDTO.getPhoneNumber();

        boolean isUserWithSameDetailsAlreadyExists=userRepository.isExistsByUsernameOrEmailOrPhoneNo(new UserIdentityDTO(receivedUserNameLC, receivedEmailLC,receivedPhoneNo));

        if(isUserWithSameDetailsAlreadyExists)
            throw new UserWithSameDetailsAlreadyExistsException();

        List<PendingSignupUser> pendingSignupUsers=pendingSignupUserRepository.findByUsernameOrEmailOrPhoneNo(new UserIdentityDTO(receivedUserNameLC,receivedEmailLC,receivedPhoneNo));

        boolean isNoPendingSignUserPresentWithSameDetails=pendingSignupUsers.isEmpty();

        if(isNoPendingSignUserPresentWithSameDetails) {
            PendingSignupUser createdPendingSignupUser;
            try {
                createdPendingSignupUser =
                        createAndReturnPendingSignupUser(signupDataDTO);
            } catch(DataIntegrityViolationException e) {
                throw new PendingSignupUserWithSameDetailsAlreadyExistsException();
            }
            createSignupOtpsForCreatedPendingSignupUser(createdPendingSignupUser);
            return;
        }

        if(!isOtpsForAllPendingSignupUsersExpired(pendingSignupUsers)) {
            throw new PendingSignupUserWithSameDetailsAlreadyExistsException();
        }

        try {
            deletePendingSignupUsersWhoseOtpsWereExpired(pendingSignupUsers);
        } catch (InvalidDataAccessApiUsageException e) {
            throw new PendingSignupUserWithSameDetailsAlreadyExistsException();
        }
        PendingSignupUser createdPendingSignupUser=
                createAndReturnPendingSignupUser(signupDataDTO);
        createSignupOtpsForCreatedPendingSignupUser(createdPendingSignupUser);
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
