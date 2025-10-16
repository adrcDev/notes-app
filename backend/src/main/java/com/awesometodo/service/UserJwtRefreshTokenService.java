package com.awesometodo.service;

import com.awesometodo.dto.JwtAuthTokensDTO;
import com.awesometodo.entity.JwtRefreshToken;
import com.awesometodo.entity.User;
import com.awesometodo.exception.InvalidJwtRefreshTokenException;
import com.awesometodo.exception.JwtRefreshTokenStatusNotValidException;
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
import java.util.Optional;
import java.util.UUID;

@Service
public class UserJwtRefreshTokenService {
    private static final Logger logger= LoggerFactory.getLogger(UserJwtRefreshTokenService.class);
    private JwtRefreshTokenRepository jwtRefreshTokenRepository;
    private JwtService jwtService;
    private Argon2PasswordEncoder argon2IdPasswordEncoder;

    public UserJwtRefreshTokenService(JwtRefreshTokenRepository jwtRefreshTokenRepository,JwtService jwtService,Argon2PasswordEncoder argon2IdPasswordEncoder) {
        this.jwtRefreshTokenRepository=jwtRefreshTokenRepository;
        this.jwtService=jwtService;
        this.argon2IdPasswordEncoder=argon2IdPasswordEncoder;
    }


    @Retryable(maxAttempts = 5,backoff = @Backoff(300L),retryFor = {PessimisticLockingFailureException.class},recover = "refreshRecoveryMethod")
    @Transactional(isolation = Isolation.REPEATABLE_READ,noRollbackFor = {JwtRefreshTokenStatusNotValidException.class})
    public JwtAuthTokensDTO refresh(String cookieValue) {
        if(!jwtService.isValidJwtRefreshToken(cookieValue))
            throw new InvalidJwtRefreshTokenException();

        String validJwtRefreshToken=cookieValue;
        String jtiClaimValue=jwtService.parseJtiClaimValue(validJwtRefreshToken);
        int associatedUserId=Integer.parseInt(jwtService.parseSubjectClaimValue(validJwtRefreshToken));
        UUID jtiClaimValueAsUUID=UUID.fromString(jtiClaimValue);
        //need to do row lock in this query
        Optional<JwtRefreshToken> optional=jwtRefreshTokenRepository.findByJtiClaimValueUUIDAndUserId(jtiClaimValueAsUUID,associatedUserId,true);
        JwtRefreshToken storedJwtRefreshToken=optional.get();

        JwtRefreshToken.Status jwtRefreshTokenStatus=storedJwtRefreshToken.getStatus();
        boolean isJwtRefreshTokenStatusNotValid=
                (jwtRefreshTokenStatus==JwtRefreshToken.Status.INVALIDATED) || (jwtRefreshTokenStatus==JwtRefreshToken.Status.COMPROMISED);
        if(isJwtRefreshTokenStatusNotValid) {
            User associatedUser=storedJwtRefreshToken.getUserAssociatedWithRefreshToken();
            jwtRefreshTokenRepository.updateStatusOfAllJwtRefreshTokensForUserId(associatedUser.getId(), JwtRefreshToken.Status.COMPROMISED);
            /* You can use a external service here to send an sms or email here to the user notifying them that a person was trying to access their account and so therefore as a security measure all of their existing logins were auto logged out
            */
            throw new JwtRefreshTokenStatusNotValidException();
        }

        storedJwtRefreshToken.setStatus(JwtRefreshToken.Status.INVALIDATED);
        User assoicatedUser=storedJwtRefreshToken.getUserAssociatedWithRefreshToken();
        int associatedUsersId=assoicatedUser.getId();
        String newStoredJwtRefreshToken=createAndReturnJwtRefreshTokenForUser(assoicatedUser);
        String jwtAccessToken=jwtService.generateJwtAccessToken(associatedUsersId);
        JwtAuthTokensDTO jwtAuthTokensDTO=new JwtAuthTokensDTO(jwtAccessToken,newStoredJwtRefreshToken);
        return jwtAuthTokensDTO;
    }

    @Recover
    private JwtAuthTokensDTO refreshRecoveryMethod(PessimisticLockingFailureException e,String cookieValue) {
        logger.error("The refresh method was retried multiple times but still a serialization anomaly kept being detected by the database");
        throw e;
    }
    
    private String createAndReturnJwtRefreshTokenForUser(User user) {
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


}
