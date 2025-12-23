package com.awesometodo.service;

import com.awesometodo.dto.JwtAuthTokensDTO;
import com.awesometodo.entity.InvalidatedJwtRefreshTokenExtensionPeriod;
import com.awesometodo.entity.JwtRefreshToken;
import com.awesometodo.entity.User;
import com.awesometodo.exception.InvalidJwtRefreshTokenException;
import com.awesometodo.exception.JwtRefreshTokenStatusNotValidException;
import com.awesometodo.repository.InvalidatedJwtRefreshTokenExtensionPeriodRepository;
import com.awesometodo.repository.JwtRefreshTokenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserJwtRefreshService {
    private static final Logger logger= LoggerFactory.getLogger(UserJwtRefreshService.class);
    private JwtRefreshTokenRepository jwtRefreshTokenRepository;
    private InvalidatedJwtRefreshTokenExtensionPeriodRepository invalidatedJwtRefreshTokenExtensionPeriodRepository;
    private JwtService jwtService;
    private Argon2PasswordEncoder argon2IdPasswordEncoder;

    public UserJwtRefreshService(JwtRefreshTokenRepository jwtRefreshTokenRepository, JwtService jwtService, Argon2PasswordEncoder argon2IdPasswordEncoder,InvalidatedJwtRefreshTokenExtensionPeriodRepository invalidatedJwtRefreshTokenExtensionPeriodRepository) {
        this.jwtRefreshTokenRepository=jwtRefreshTokenRepository;
        this.jwtService=jwtService;
        this.argon2IdPasswordEncoder=argon2IdPasswordEncoder;
        this.invalidatedJwtRefreshTokenExtensionPeriodRepository=invalidatedJwtRefreshTokenExtensionPeriodRepository;
    }


    @Retryable(maxAttempts = 5,backoff = @Backoff(300L),retryFor = {PessimisticLockingFailureException.class},recover = "refreshRecoveryMethod")
    @Transactional(isolation = Isolation.REPEATABLE_READ,noRollbackFor = {JwtRefreshTokenStatusNotValidException.class})
    public JwtAuthTokensDTO refresh(String cookieValue) {
        logger.debug("Checking if the cookie's value is a valid jwt refresh token");
        if(!jwtService.isValidJwtRefreshToken(cookieValue)) {
            logger.warn("The cookie's value is not a valid jwt refresh token. Aborting jwt authentication refresh process");
            throw new InvalidJwtRefreshTokenException();
        }

        logger.debug("The cookie's value is a valid jwt refresh token");
        String validJwtRefreshToken=cookieValue;
        String jtiClaimValue=jwtService.parseJtiClaimValue(validJwtRefreshToken);
        int associatedUserId=Integer.parseInt(jwtService.parseSubjectClaimValue(validJwtRefreshToken));
        UUID jtiClaimValueAsUUID=UUID.fromString(jtiClaimValue);
        Optional<JwtRefreshToken> optional=jwtRefreshTokenRepository.findByJtiClaimValueUUIDAndUserId(jtiClaimValueAsUUID,associatedUserId,true);
        JwtRefreshToken storedJwtRefreshToken=optional.get();
        logger.debug("Obtained the corresponding jwt refresh token row using the received jwt refresh token and put a row lock on it. It is associated to user with id:{}, username:{}, email:{}",storedJwtRefreshToken.getUserAssociatedWithRefreshToken().getId(),storedJwtRefreshToken.getUserAssociatedWithRefreshToken().getUserName(),storedJwtRefreshToken.getUserAssociatedWithRefreshToken().getEmail());

        JwtRefreshToken.Status jwtRefreshTokenStatus=storedJwtRefreshToken.getStatus();
        boolean isJwtRefreshTokenStatusValueIsCompromised=
                 (jwtRefreshTokenStatus==JwtRefreshToken.Status.COMPROMISED);
        if(isJwtRefreshTokenStatusValueIsCompromised) {
            User associatedUser=storedJwtRefreshToken.getUserAssociatedWithRefreshToken();
            logger.warn("The jwt refresh token row's status value is 'compromised' so this means that a jwt refresh token is being reused which means that an attacker probably got hold of a jwt refresh token therefore as a security measure,trying to set the status of all the jwt refresh tokens of the associated user to 'compromised' and associated user has id:{}",associatedUser.getId());
            setStatusAsCompromisedForAllJwtRefreshTokensAssociatedToUser(associatedUser);
            throw new JwtRefreshTokenStatusNotValidException();
        }

        boolean isJwtRefreshTokenStatusValueIsInvalidated=
                (jwtRefreshTokenStatus==JwtRefreshToken.Status.INVALIDATED);
        if(isJwtRefreshTokenStatusValueIsInvalidated) {
            User associatedUser=storedJwtRefreshToken.getUserAssociatedWithRefreshToken();
            int invalidatedJwtRefreshTokenId=storedJwtRefreshToken.getId();
            Optional<Boolean> optionalBoolean=invalidatedJwtRefreshTokenExtensionPeriodRepository.isExtensionPeriodExpiredForInvalidatedJwtRefreshTokenId(invalidatedJwtRefreshTokenId);
            boolean isExtensionPeriodExpired;
            try {
                isExtensionPeriodExpired = optionalBoolean.orElseThrow();
            } catch(NoSuchElementException e) {
                logger.warn("The jwt refresh token doesn't have an associated row in the invalidated_jwt_refresh_token_extension_periods table,so this means that a jwt refresh token that was set to status of 'invalidated' by the logout endpoint is being reused which means that an attacker probably got hold of a jwt refresh token therefore as a security measure,trying to set the status of all the jwt refresh tokens of the associated user to 'compromised' and associated user has id:{}",associatedUser.getId());
                setStatusAsCompromisedForAllJwtRefreshTokensAssociatedToUser(associatedUser);
                throw new JwtRefreshTokenStatusNotValidException();
            }

            if(isExtensionPeriodExpired) {
                logger.warn("The jwt refresh token row's status value is 'invalidated' and it's extension period has expired, so this means that a jwt refresh token is being reused which means that an attacker probably got hold of a jwt refresh token therefore as a security measure,trying to set the status of all the jwt refresh tokens of the associated user to 'compromised' and associated user has id:{}",associatedUser.getId());
                setStatusAsCompromisedForAllJwtRefreshTokensAssociatedToUser(associatedUser);
                throw new JwtRefreshTokenStatusNotValidException();
            }

            String jwtAcessToken=jwtService.generateJwtAccessToken(associatedUserId);
            String newJwtRefreshToken=createStoreAndReturnJwtRefreshTokenForUser(storedJwtRefreshToken.getUserAssociatedWithRefreshToken());
            return new JwtAuthTokensDTO(jwtAcessToken,newJwtRefreshToken);
        }

        logger.debug("The jwt refresh token row for user with id:{}, username:{} and email:{} has a status value of 'valid'. Trying to change the status value to 'invalidated'",associatedUserId,storedJwtRefreshToken.getUserAssociatedWithRefreshToken().getUserName(),storedJwtRefreshToken.getUserAssociatedWithRefreshToken().getEmail());
        storedJwtRefreshToken.setStatus(JwtRefreshToken.Status.INVALIDATED);
        invalidatedJwtRefreshTokenExtensionPeriodRepository.insert(new InvalidatedJwtRefreshTokenExtensionPeriod(storedJwtRefreshToken));
        User assoicatedUser=storedJwtRefreshToken.getUserAssociatedWithRefreshToken();
        logger.debug("The status value of the jwt refresh token row for user with id:{}, username:{} and email:{} has been changed to 'invalidated'.",assoicatedUser.getId(),assoicatedUser.getUserName(),assoicatedUser.getEmail());
        int associatedUsersId=assoicatedUser.getId();
        logger.debug("Trying to create and store a new jwt refresh token for user with id:{},username:{} and email:{}",assoicatedUser.getId(),assoicatedUser.getUserName(),assoicatedUser.getEmail());
        String newStoredJwtRefreshToken= createStoreAndReturnJwtRefreshTokenForUser(assoicatedUser);
        logger.debug("New jwt refresh token was created and stored for user with id:{},username:{} and email:{}",assoicatedUser.getId(),assoicatedUser.getUserName(),assoicatedUser.getEmail());
        String jwtAccessToken=jwtService.generateJwtAccessToken(associatedUsersId);
        logger.debug("A jwt access token was generated for user with id:{},username:{} and email:{}",assoicatedUser.getId(),assoicatedUser.getUserName(),assoicatedUser.getEmail());
        JwtAuthTokensDTO jwtAuthTokensDTO=new JwtAuthTokensDTO(jwtAccessToken,newStoredJwtRefreshToken);
        return jwtAuthTokensDTO;
    }

    @Recover
    private JwtAuthTokensDTO refreshRecoveryMethod(PessimisticLockingFailureException e,String cookieValue) {
        logger.error("The refresh method was retried multiple times but still a serialization anomaly kept being detected by the database");
        throw e;
    }

    private String createStoreAndReturnJwtRefreshTokenForUser(User user) {
        int userId=user.getId();
        String jwtRefreshTokenString=jwtService.generateJwtRefreshToken(userId);
        String jwtRefreshTokenHash=argon2IdPasswordEncoder.encode(jwtRefreshTokenString);
        String jtiClaimValue=jwtService.parseJtiClaimValue(jwtRefreshTokenString);
        UUID jtiClaimValueAsUUID=UUID.fromString(jtiClaimValue);
        OffsetDateTime issuedAt=jwtService.parseIssClaimValue(jwtRefreshTokenString);
        OffsetDateTime expiresAt=jwtService.parseExpClaimValue(jwtRefreshTokenString);
        JwtRefreshToken jwtRefreshToken=new JwtRefreshToken(user,jtiClaimValueAsUUID,jwtRefreshTokenHash, JwtRefreshToken.Status.VALID,issuedAt,expiresAt);
        jwtRefreshTokenRepository.insert(jwtRefreshToken);
        return jwtRefreshTokenString;
    }

    private void setStatusAsCompromisedForAllJwtRefreshTokensAssociatedToUser(User associatedUser) {
        jwtRefreshTokenRepository.updateStatusOfAllJwtRefreshTokensForUserId(associatedUser.getId(), JwtRefreshToken.Status.COMPROMISED);
        /* You can use a external service here to send an sms or email here to the user notifying them that a person was trying to access their account and so therefore as a security measure all of their existing logins were auto logged out
         */
        logger.warn("All stored jwt refresh tokens belong to user with id:{} have been set with a status value of 'compromised' and thus the user has been logged out of all his current logins. Aborting the jwt authentication refresh process",associatedUser.getId());
    }


}
