package com.awesometodo.service;

import com.awesometodo.entity.JwtRefreshToken;
import com.awesometodo.entity.User;
import com.awesometodo.exception.InvalidJwtRefreshTokenException;
import com.awesometodo.exception.InvalidatedStatusJwtRefreshTokenException;
import com.awesometodo.repository.JwtRefreshTokenRepository;
import com.awesometodo.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserLogoutService {
    private static final Logger logger= LoggerFactory.getLogger(UserLogoutService.class);
    private UserRepository userRepository;
    private JwtRefreshTokenRepository jwtRefreshTokenRepository;
    private JwtService jwtService;

    public UserLogoutService(UserRepository userRepository, JwtRefreshTokenRepository jwtRefreshTokenRepository, JwtService jwtService) {
        this.userRepository = userRepository;
        this.jwtRefreshTokenRepository = jwtRefreshTokenRepository;
        this.jwtService = jwtService;
    }

    @Retryable(maxAttempts = 5,backoff = @Backoff(300L),retryFor = {PessimisticLockingFailureException.class},recover = "logoutRecoveryMethod")
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void logout(String cookieValue) {
        logger.debug("Checking if the cookie's value is a valid jwt refresh token");
        if(!jwtService.isValidJwtRefreshToken(cookieValue)) {
            logger.warn("The cookie's value is not a valid jwt refresh token. Aborting logout process");
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
        boolean isJwtRefreshTokenStatusOfInvalidated=
                (jwtRefreshTokenStatus==JwtRefreshToken.Status.INVALIDATED);
        if(isJwtRefreshTokenStatusOfInvalidated) {
            logger.warn("The jwt refresh token row's status is 'invalidated'. Aborting the logout process");
            throw new InvalidatedStatusJwtRefreshTokenException();
        }

        logger.debug("The jwt refresh token row for user with id:{}, username:{} and email:{} has a status value of 'valid'. Trying to change the status value to 'invalidated'",associatedUserId,storedJwtRefreshToken.getUserAssociatedWithRefreshToken().getUserName(),storedJwtRefreshToken.getUserAssociatedWithRefreshToken().getEmail());
        storedJwtRefreshToken.setStatus(JwtRefreshToken.Status.INVALIDATED);
        User assoicatedUser=storedJwtRefreshToken.getUserAssociatedWithRefreshToken();
        logger.debug("The status value of the jwt refresh token row for user with id:{}, username:{} and email:{} has been changed to 'invalidated'.",assoicatedUser.getId(),assoicatedUser.getUserName(),assoicatedUser.getEmail());
    }

    @Recover
    private void logoutRecoveryMethod(PessimisticLockingFailureException e,String cookieValue) {
        logger.error("The logout method was retried multiple times but still a serialization anomaly kept being detected by the database");
        throw e;
    }
}
