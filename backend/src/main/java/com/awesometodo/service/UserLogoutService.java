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

    /* Known limitation:-
    If a jwt refresh token with 'compromised' status value or 'invalidated' status value is received then all
    of the user's associated jwt refresh token's will be set to a status value of 'compromised' thereby
    logging out the user from all his logins. The problem is that some of these jwt refresh tokens will
    still be stored in the user's browser's cookie storage.Assume a user who has been logged out of all his logins goes to any page of the website which will cause the /auth/v1/refresh endpoint to be called, then he will receive a 401 response and will then be taken to login page in the frontend so that he can re-login. After he logs in he gets new jwt refresh token cookie in his browser.  After this assume
    the user tries to access the website on one his older devices which contains
    a 'compromised' status valued refresh token in the cookie as he had logged in before, the page will use
    the /auth/v1/refresh endpoint and again cause the user to be logged out of all his logins.
    */
    @Retryable(maxAttempts = 5,backoff = @Backoff(300L),retryFor = {PessimisticLockingFailureException.class},recover = "logoutRecoveryMethod")
    @Transactional(isolation = Isolation.REPEATABLE_READ,noRollbackFor = {InvalidatedStatusJwtRefreshTokenException.class})
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
        logger.debug("Obtained the corresponding jwt refresh token row using the received jwt refresh token. It is associated to user with id:{}, username:{}, email:{}",storedJwtRefreshToken.getUserAssociatedWithRefreshToken().getId(),storedJwtRefreshToken.getUserAssociatedWithRefreshToken().getUserName(),storedJwtRefreshToken.getUserAssociatedWithRefreshToken().getEmail());

        JwtRefreshToken.Status jwtRefreshTokenStatus=storedJwtRefreshToken.getStatus();
        boolean isJwtRefreshTokenStatusNotValid=
                (jwtRefreshTokenStatus==JwtRefreshToken.Status.INVALIDATED);
        if(isJwtRefreshTokenStatusNotValid) {
            User associatedUser=storedJwtRefreshToken.getUserAssociatedWithRefreshToken();
            logger.warn("The jwt refresh token row's status is not 'valid' so this means that a jwt refresh token is being reused which means that an attacker probably got hold of a jwt refresh token therefore as a security measure,trying to set the status of all the jwt refresh tokens of the associated user to 'compromised' and associated user has id:{}",associatedUser.getId());
//            jwtRefreshTokenRepository.updateStatusOfAllJwtRefreshTokensForUserId(associatedUser.getId(), JwtRefreshToken.Status.COMPROMISED);
            logger.warn("All stored jwt refresh tokens belong to user with id:{} have been set with a status value of 'compromised' and thus the user has been logged out of all his current logins. Aborting the logout process",associatedUser.getId());
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
