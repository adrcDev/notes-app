package com.awesometodo.service;

import com.awesometodo.entity.JwtRefreshToken;
import com.awesometodo.entity.User;
import com.awesometodo.repository.JwtRefreshTokenRepository;
import com.awesometodo.repository.UserRepository;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Optional;

@Service
public class JwtRefreshTokenService {
    JwtRefreshTokenRepository jwtRefreshTokenRepository;
    JwtService jwtService;
    UserRepository userRepository;
    Argon2PasswordEncoder argon2IdPasswordEncoder;

    public JwtRefreshTokenService(JwtRefreshTokenRepository jwtRefreshTokenRepository,JwtService jwtService,UserRepository userRepository,Argon2PasswordEncoder argon2IdPasswordEncoder) {
        this.jwtRefreshTokenRepository=jwtRefreshTokenRepository;
        this.jwtService=jwtService;
        this.userRepository=userRepository;
        this.argon2IdPasswordEncoder=argon2IdPasswordEncoder;
    }

    public void storeJwtRefreshToken(String jwtRefreshToken) {
        int userId=Integer.parseInt(jwtService.parseSubjectClaimValue(jwtRefreshToken));
        Optional<User> optional=userRepository.findById(userId);
        User userAssociatedToId=optional.get();
        OffsetDateTime refreshTokenIssuedAt=jwtService.parseIssClaimValue(jwtRefreshToken);
        OffsetDateTime refreshTokenExpiresAt=jwtService.parseExpClaimValue(jwtRefreshToken);
        String jwtRefreshTokenHash=argon2IdPasswordEncoder.encode(jwtRefreshToken);

        JwtRefreshToken jwtRefreshTokenEntityObj=new JwtRefreshToken(userAssociatedToId,jwtRefreshTokenHash, JwtRefreshToken.Status.VALID,refreshTokenIssuedAt,refreshTokenExpiresAt);
        jwtRefreshTokenRepository.insertJwtRefreshToken(jwtRefreshTokenEntityObj);
    }

}
