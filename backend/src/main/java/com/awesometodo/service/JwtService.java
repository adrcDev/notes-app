package com.awesometodo.service;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;

@Service
public class JwtService {
    private static final String DOMAIN_NAME_ALONG_WITH_HTTP_SCHEME="http://localhost:8080";
    private static final int JWT_ACCESS_TOKEN_EXPIRY_TIME_IN_MINUTES=20;
    private static final int JWT_ACCESS_TOKEN_EXPIRY_TIME_IN_MILLIS=
            JWT_ACCESS_TOKEN_EXPIRY_TIME_IN_MINUTES*60*1000;
    private static final String HMAC_SHA_256_SECRET_KEY=System.getenv("HMAC_SHA_256_SECRET_KEY");
    private static final byte[] hmacSHA256SecretKeyBytes = Base64.getDecoder().decode(HMAC_SHA_256_SECRET_KEY);

    public String generateJwtAccessToken(int userIdToUseAsSubjectClaimValue) {
        String jwtAccessToken= Jwts.builder().header().type("JWT")
                .and().claims().issuer(DOMAIN_NAME_ALONG_WITH_HTTP_SCHEME).subject(String.valueOf(userIdToUseAsSubjectClaimValue)).add("aud",DOMAIN_NAME_ALONG_WITH_HTTP_SCHEME).expiration(new Date(System.currentTimeMillis()+JWT_ACCESS_TOKEN_EXPIRY_TIME_IN_MILLIS)).issuedAt(new Date()).add("token_type","access")
                .and().signWith(Keys.hmacShaKeyFor(hmacSHA256SecretKeyBytes)).compact();
        return jwtAccessToken;
    }


    public String generateJwtRefreshToken(int userIdToUseAsSubjectClaimValue) {
        return "TODO";
    }

}
