package com.awesometodo.service;

import com.awesometodo.dto.JwtAuthTokensDTO;
import com.awesometodo.entity.InvalidatedJwtRefreshTokenExtensionPeriod;
import com.awesometodo.entity.JwtRefreshToken;
import com.awesometodo.entity.User;
import com.awesometodo.exception.*;
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
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public JwtAuthTokensDTO refresh(String cookieValue) {
        logger.debug("Checking if the cookie's value is a valid jwt refresh token");
        if(!jwtService.isValidJwtRefreshToken(cookieValue)) {
            logger.warn("The cookie's value is not a valid jwt refresh token. Aborting jwt authentication refresh process");
            throw new InvalidJwtRefreshTokenException();
        }

        logger.debug("The cookie's value is a valid jwt refresh token");
        String validJwtRefreshToken=cookieValue;
        String jtiClaimValue=jwtService.parseJtiClaimValue(validJwtRefreshToken);
        logger.debug("The jti claim's value:{} (it is a UUID that uniquely identified a jwt refresh token within the jwt_refresh_tokens table) was extracted from the received jwt refresh token's payload section",jtiClaimValue);
        int associatedUserId=Integer.parseInt(jwtService.parseSubjectClaimValue(validJwtRefreshToken));
        logger.debug("The sub claim's value:{} (it is user id) was extracted from the received jwt refresh token's payload section",associatedUserId);
        UUID jtiClaimValueAsUUID=UUID.fromString(jtiClaimValue);
        Optional<JwtRefreshToken> optional=jwtRefreshTokenRepository.findByJtiClaimValueUUIDAndUserId(jtiClaimValueAsUUID,associatedUserId,true);
        JwtRefreshToken storedJwtRefreshToken=optional.get();
        logger.debug("Obtained the corresponding jwt refresh token row using the extracted jti claim value({}) and sub claim value({}) from jwt_refresh_tokens table and put a row lock on it. It is associated to user with id:{}, username:{}",jtiClaimValue,associatedUserId,storedJwtRefreshToken.getUserAssociatedWithRefreshToken().getId(),storedJwtRefreshToken.getUserAssociatedWithRefreshToken().getUserName());

        JwtRefreshToken.Status jwtRefreshTokenStatus=storedJwtRefreshToken.getStatus();
        boolean isJwtRefreshTokenStatusValueIsInvalidated=
                (jwtRefreshTokenStatus==JwtRefreshToken.Status.INVALIDATED);
        if(isJwtRefreshTokenStatusValueIsInvalidated) {
            logger.debug("The jwt refresh token row's status value is 'invalidated'");
            User associatedUser=storedJwtRefreshToken.getUserAssociatedWithRefreshToken();
            int invalidatedJwtRefreshTokenId=storedJwtRefreshToken.getId();
            logger.debug("Checking whether the 'invalidated' status valued jwt refresh token's associated extension period in the invalidated_jwt_refresh_token_extension_periods table is expired");
            Optional<Boolean> optionalBoolean=invalidatedJwtRefreshTokenExtensionPeriodRepository.isExtensionPeriodExpiredForInvalidatedJwtRefreshTokenId(invalidatedJwtRefreshTokenId);
            boolean isExtensionPeriodExpired;
            try {
                isExtensionPeriodExpired = optionalBoolean.orElseThrow();
            } catch(NoSuchElementException e) {
                logger.warn("The jwt refresh token doesn't have an associated row in the invalidated_jwt_refresh_token_extension_periods table,so this means that a jwt refresh token is being reused. Aborting the jwt authentication refresh process");
                throw new InvalidatedStatusJwtRefreshTokenNoExtensionPeriodException();
            }

            if(isExtensionPeriodExpired) {
                logger.warn("The jwt refresh token row's status value is 'invalidated' and it's extension period has expired, so this means that a jwt refresh token is being reused.Aborting the jwt authentication refresh process");
                throw new InvalidatedStatusJwtRefreshTokenExtensionPeriodExpiredException();
            }

            logger.debug("The jwt refresh token row's status value is 'invalidated' and it's extension period has not expired");
            String jwtAccessToken=jwtService.generateJwtAccessToken(associatedUserId);
            logger.debug("Jwt access token was created for user id {}",associatedUserId);
            String newJwtRefreshToken=createStoreAndReturnJwtRefreshTokenForUser(storedJwtRefreshToken.getUserAssociatedWithRefreshToken());
            return new JwtAuthTokensDTO(jwtAccessToken,newJwtRefreshToken);
        }

        User assoicatedUser=storedJwtRefreshToken.getUserAssociatedWithRefreshToken();
        logger.debug("The jwt refresh token row for user with id:{}, username:{} and email:{} has a status value of 'valid'. Trying to change the status value to 'invalidated'",associatedUserId,assoicatedUser.getUserName(),assoicatedUser.getEmail());
        storedJwtRefreshToken.setStatus(JwtRefreshToken.Status.INVALIDATED);
        logger.debug("The status value of the jwt refresh token row for user with id:{} and username:{} has been changed to 'invalidated'.",assoicatedUser.getId(),assoicatedUser.getUserName());
        invalidatedJwtRefreshTokenExtensionPeriodRepository.insert(new InvalidatedJwtRefreshTokenExtensionPeriod(storedJwtRefreshToken));
        logger.debug("An extension period row was added to the invalidated_jwt_refresh_token_extension_periods table for the just newly changed to status value 'invalidated' jwt refresh token");
        int associatedUsersId=assoicatedUser.getId();
        logger.debug("Trying to create and store a new jwt refresh token for user with id:{},username:{} ",assoicatedUser.getId(),assoicatedUser.getUserName());
        String newStoredJwtRefreshToken= createStoreAndReturnJwtRefreshTokenForUser(assoicatedUser);
        String jwtAccessToken=jwtService.generateJwtAccessToken(associatedUsersId);
        logger.debug("A jwt access token was generated for user with id:{},username:{}",assoicatedUser.getId(),assoicatedUser.getUserName());
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
        logger.debug("New jwt refresh token was created associated to user id {}",userId);
        String jwtRefreshTokenHash=argon2IdPasswordEncoder.encode(jwtRefreshTokenString);
        logger.debug("Hashed the jwt refresh token using Argon2Id hashing");
        String jtiClaimValue=jwtService.parseJtiClaimValue(jwtRefreshTokenString);
        UUID jtiClaimValueAsUUID=UUID.fromString(jtiClaimValue);
        logger.debug("Parsed the jti claim value({}) from the newly created jwt refresh token and converted it to a UUID object",jtiClaimValue);
        OffsetDateTime issuedAt=jwtService.parseIssClaimValue(jwtRefreshTokenString);
        OffsetDateTime expiresAt=jwtService.parseExpClaimValue(jwtRefreshTokenString);
        logger.debug("Parsed iss and exp claims from the newly created jwt refresh token as OffsetDateTime objects");
        JwtRefreshToken jwtRefreshToken=new JwtRefreshToken(user,jtiClaimValueAsUUID,jwtRefreshTokenHash, JwtRefreshToken.Status.VALID,issuedAt,expiresAt);
        jwtRefreshTokenRepository.insert(jwtRefreshToken);
        logger.debug("Inserted the newly created 'valid' status valued jwt refresh token in the jwt_refresh_tokens table");
        return jwtRefreshTokenString;
    }



}
