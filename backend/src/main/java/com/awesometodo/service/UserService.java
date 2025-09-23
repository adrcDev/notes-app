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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private final static Logger logger= LoggerFactory.getLogger(UserService.class);
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
        String email="";
        String userName="";
        if(isEmail) {
            email=userNameOrEmail;
            logger.debug("Login is being attempted by email {}",email);
            logger.debug("Trying to find an existing user account with email:{}",email);
            optional=userRepository.findByEmail(email);
        }
        else {
            userName=userNameOrEmail;
            logger.debug("Login is being attempted by username {}",userName);
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

    @Transactional
    public void signupInitialization(SignupDataDTO signupDataDTO) {
        String receivedUserNameLC=signupDataDTO.getUserName().toLowerCase();
        String receivedEmailLC=signupDataDTO.getEmail().toLowerCase();
        String receivedPhoneNo=signupDataDTO.getPhoneNumber();

        logger.debug("Signup attempt made with username:{}, email:{}",receivedUserNameLC,receivedEmailLC);
        logger.debug("Checking if any user account already exists in database who have same username or email or phone number as the received username:{}, email:{} and phone number",receivedUserNameLC,receivedEmailLC);
        boolean isUserWithSameDetailsAlreadyExists=userRepository.isExistsByUsernameOrEmailOrPhoneNo(new UserIdentityDTO(receivedUserNameLC, receivedEmailLC,receivedPhoneNo));

        if(isUserWithSameDetailsAlreadyExists) {
            logger.warn("Signup attempt failed as the received username:{} or email:{} or phone number is already in use by an existing user account in the database",receivedUserNameLC,receivedEmailLC);
            throw new UserWithSameDetailsAlreadyExistsException();
        }

        logger.debug("No currently present user account in the database uses the received username:{} or email:{} or phone number, so the signup process continues",receivedUserNameLC,receivedEmailLC);
        logger.debug("Checking if any pending sign up user already exists in database who have same username or email or phone number as the received username:{}, email:{} and phone number",receivedUserNameLC,receivedEmailLC);
        List<PendingSignupUser> pendingSignupUsers=pendingSignupUserRepository.findByUsernameOrEmailOrPhoneNo(new UserIdentityDTO(receivedUserNameLC,receivedEmailLC,receivedPhoneNo));

        boolean isNoPendingSignUserPresentWithSameDetails=pendingSignupUsers.isEmpty();

        if(isNoPendingSignUserPresentWithSameDetails) {
            logger.debug("No pending signup user existed with same username or email or phone number as received username:{}, email:{} and phone number so new pending signup user will be created and phone number otp and email otp will also be generated,stored and sent",receivedUserNameLC,receivedEmailLC);
            PendingSignupUser createdPendingSignupUser;
            try {
                createdPendingSignupUser =
                        createAndReturnPendingSignupUser(signupDataDTO);
                logger.info("New pending signup user created with id:{},username:{}, and email:{}",createdPendingSignupUser.getId(),createdPendingSignupUser.getUserName(),createdPendingSignupUser.getEmail());
            } catch(DataIntegrityViolationException e) {
                logger.warn("Pending signup user creation process failed for user with username:{} and email:{} due to concurrent execution by a user with the same username or email or phone number",receivedUserNameLC,receivedEmailLC);
                throw new PendingSignupUserWithSameDetailsAlreadyExistsException();
            }
            createSignupOtpsForCreatedPendingSignupUser(createdPendingSignupUser);
            logger.info("Phone number and email otps were generated,stored and sent for newly created pending signup user with id:{}, username:{} and email:{}",createdPendingSignupUser.getId(),createdPendingSignupUser.getUserName(),createdPendingSignupUser.getEmail());
            return;
        }

        logger.debug("Atleast one pending signup user with same username or email or phone number as the received username:{},email:{} and phone number already exists in the database. Trying to check whether all of them are expired.",receivedUserNameLC,receivedEmailLC);

        if(!isOtpsForAllPendingSignupUsersExpired(pendingSignupUsers)) {
            logger.warn("Pending signup user creation process failed for user with username:{} and email:{} as atleast one pending signup user with the same username or email or phone number and non expired otp's already exists in the database",receivedUserNameLC,receivedUserNameLC);
            throw new PendingSignupUserWithSameDetailsAlreadyExistsException();
        }

        logger.debug("All the pending sign up users in the database who had same username or email or phone number as the received username:{}, email{} and phone number, are expired.",receivedUserNameLC,receivedEmailLC);
        try {
            logger.debug("Trying to delete all the {}  pending signup users whose otp's have expired",pendingSignupUsers.size());
            deletePendingSignupUsersWhoseOtpsWereExpired(pendingSignupUsers);
            logger.debug("All {} pending signup up users whose otp's had expired were deleted succesfully",pendingSignupUsers.size());
        } catch (InvalidDataAccessApiUsageException e) {
            logger.warn("Pending signup user creation process failed for user with username:{} and emai:{} due to concurrent execution of the creation process by a user with the same username or email or phone number",receivedUserNameLC,receivedEmailLC);
            throw new PendingSignupUserWithSameDetailsAlreadyExistsException();
        }

        PendingSignupUser createdPendingSignupUser=
                createAndReturnPendingSignupUser(signupDataDTO);
        logger.info("New pending signup user created with id:{},username:{}, and email:{}",createdPendingSignupUser.getId(),createdPendingSignupUser.getUserName(),createdPendingSignupUser.getEmail());
        createSignupOtpsForCreatedPendingSignupUser(createdPendingSignupUser);
        logger.info("Phone number and email otps were generated,stored and sent for newly created pending signup user with id:{}, username:{} and email:{}",createdPendingSignupUser.getId(),createdPendingSignupUser.getUserName(),createdPendingSignupUser.getEmail());
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
