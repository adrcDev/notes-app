package com.awesometodo.service;

import com.awesometodo.entity.JwtRefreshToken;
import com.awesometodo.repository.JwtRefreshTokenRepository;
import com.awesometodo.repository.UserRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.*;

@Service
public class JwtService {
    private static final String DOMAIN_NAME_ALONG_WITH_HTTP_SCHEME="http://localhost:8080";
    private static final int JWT_ACCESS_TOKEN_EXPIRY_TIME_IN_MINUTES=20;
    private static final int JWT_ACCESS_TOKEN_EXPIRY_TIME_IN_MILLIS=
            JWT_ACCESS_TOKEN_EXPIRY_TIME_IN_MINUTES*60*1000;
    private static final int JWT_REFRESH_TOKEN_EXPIRY_TIME_IN_DAYS=31;
    private static final long JWT_REFRESH_TOKEN_EXPIRY_TIME_IN_MILLIS=
            (long) JWT_REFRESH_TOKEN_EXPIRY_TIME_IN_DAYS *24*60*60*1000;
    private static final String HMAC_SHA_256_SECRET_KEY=System.getenv("HMAC_SHA_256_SECRET_KEY");
    private static final byte[] hmacSHA256SecretKeyBytes = Base64.getDecoder().decode(HMAC_SHA_256_SECRET_KEY);
    private UserRepository userRepository;
    private JwtRefreshTokenRepository jwtRefreshTokenRepository;
    private Argon2PasswordEncoder argon2IdPasswordEncoder;

    public JwtService(UserRepository userRepository, JwtRefreshTokenRepository jwtRefreshTokenRepository, Argon2PasswordEncoder argon2IdPasswordEncoder) {
        this.userRepository=userRepository;
        this.jwtRefreshTokenRepository=jwtRefreshTokenRepository;
        this.argon2IdPasswordEncoder=argon2IdPasswordEncoder;
    }

    public String generateJwtAccessToken(int userIdToUseAsSubjectClaimValue) {
        String jwtAccessToken= Jwts.builder().header().type("JWT")
                .and().claims().issuer(DOMAIN_NAME_ALONG_WITH_HTTP_SCHEME).subject(String.valueOf(userIdToUseAsSubjectClaimValue)).add("aud",DOMAIN_NAME_ALONG_WITH_HTTP_SCHEME).expiration(new Date(System.currentTimeMillis()+JWT_ACCESS_TOKEN_EXPIRY_TIME_IN_MILLIS)).issuedAt(new Date()).add("token_type","access")
                .and().signWith(Keys.hmacShaKeyFor(hmacSHA256SecretKeyBytes)).compact();
        return jwtAccessToken;
    }


    public String generateJwtRefreshToken(int userIdToUseAsSubjectClaimValue) {
        String jtiClaimValue=UUID.randomUUID().toString();
        String jwtRefreshToken=Jwts.builder().header().type("JWT").and().claims().id(jtiClaimValue).issuer(DOMAIN_NAME_ALONG_WITH_HTTP_SCHEME).subject(String.valueOf(userIdToUseAsSubjectClaimValue)).add("aud",DOMAIN_NAME_ALONG_WITH_HTTP_SCHEME).expiration(new Date(System.currentTimeMillis()+JWT_REFRESH_TOKEN_EXPIRY_TIME_IN_MILLIS)).issuedAt(new Date()).add("token_type","refresh").and().signWith(Keys.hmacShaKeyFor(hmacSHA256SecretKeyBytes)).compact();
        return jwtRefreshToken;
    }

    public String parseSubjectClaimValue(String jwtToken) {
        String jwtConstructedForParsingClaims=removeSignatureAndAddDummyHeaderToJwt(jwtToken);
        return Jwts.parser().unsecured().build().parseUnsecuredClaims(jwtConstructedForParsingClaims).getPayload().getSubject();
    }

    public OffsetDateTime parseIssClaimValue(String jwtToken) {
        String jwtConstructedForParsingClaims=removeSignatureAndAddDummyHeaderToJwt(jwtToken);
        Date issClaimValueAsDate=Jwts.parser().unsecured().build().parseUnsecuredClaims(jwtConstructedForParsingClaims).getPayload().getIssuedAt();
        OffsetDateTime issClaimValueAsODT=
                issClaimValueAsDate.toInstant().atZone(ZoneId.systemDefault()).toOffsetDateTime();
        return  issClaimValueAsODT;
    }

    public OffsetDateTime parseExpClaimValue(String jwtToken) {
        String jwtConstructedForParsingClaims=removeSignatureAndAddDummyHeaderToJwt(jwtToken);
        Date expClaimValueAsDate=Jwts.parser().unsecured().build().parseUnsecuredClaims(jwtConstructedForParsingClaims).getPayload().getExpiration();
        OffsetDateTime expClaimValueAsODT=
                expClaimValueAsDate.toInstant().atZone(ZoneId.systemDefault()).toOffsetDateTime();
        return  expClaimValueAsODT;
    }

    public String parseJtiClaimValue(String jwtToken) {
        String jwtConstructedForParsingClaims=removeSignatureAndAddDummyHeaderToJwt(jwtToken);
        String jtiClaimValue=Jwts.parser().unsecured().build().parseUnsecuredClaims(jwtConstructedForParsingClaims).getPayload().getId();
        return jtiClaimValue;
    }

    public int getRemainingTokenLifeTimeInSeconds(String jwtToken) {
        String jwtConstructedForParsingClaims=removeSignatureAndAddDummyHeaderToJwt(jwtToken);
        Date expClaimValueAsDate=Jwts.parser().unsecured().build().parseUnsecuredClaims(jwtConstructedForParsingClaims).getPayload().getExpiration();
        long expClaimValueInMillis=expClaimValueAsDate.getTime();
        long currentTimeInMillis=System.currentTimeMillis();
        if(expClaimValueInMillis < currentTimeInMillis)
            return 0;

        long remainingTokenLifeTimeInMillis=expClaimValueInMillis-currentTimeInMillis;
        int remainingTokenLifeTimeInSeconds=(int)(remainingTokenLifeTimeInMillis/1000);

        return remainingTokenLifeTimeInSeconds;
    }

    /* It was required to perform these operations on jwt token as otherwise the jjwt library would not allow to parse the jwt token without the hmac sha 256 secret key as jjwt used to do automatic signature verification. After the transformation done by this method the jjwt library skips the signature check */
    private String removeSignatureAndAddDummyHeaderToJwt(String jwtToken) {
        String dummyJwtHeader=
                Jwts.builder().header().add("alg","none").and().compact().replace('.',' ').trim();
        String jwtTokenWithoutSignature=jwtToken.substring(0,jwtToken.lastIndexOf('.'));
        String jwtTokenPayload=jwtTokenWithoutSignature.substring(jwtTokenWithoutSignature.indexOf('.')+1);
        String jwtConstructedForParsingClaims= dummyJwtHeader+'.'+jwtTokenPayload+'.';
        return jwtConstructedForParsingClaims;
    }

    public Optional<Jws<Claims>> checkIfStringIsValidJwtAccessTokenAndReturnJwsObj(String stringToBeChecked) {
        JwtParser jwtAccessTokenParser=Jwts.parser().requireIssuer(DOMAIN_NAME_ALONG_WITH_HTTP_SCHEME).requireAudience(DOMAIN_NAME_ALONG_WITH_HTTP_SCHEME).require("token_type","access").verifyWith(Keys.hmacShaKeyFor(hmacSHA256SecretKeyBytes)).build();
        Jws<Claims> parsedToken;
        try {
            parsedToken=jwtAccessTokenParser.parseSignedClaims(stringToBeChecked);
        } catch(Exception e) {
            return Optional.empty();
        }

        boolean isContainsTypKeyWithValueJwtInHeader=parsedToken.getHeader().getType().equals("JWT");
        if(!isContainsTypKeyWithValueJwtInHeader) {
            return Optional.empty();
        }

        boolean isContainsAlgKeyWithValueHS256InHeader=parsedToken.getHeader().getAlgorithm().equals("HS256");
        if(!isContainsAlgKeyWithValueHS256InHeader) {
            return Optional.empty();
        }

        String subClaimValue=parsedToken.getPayload().getSubject();
        int userId=0;
        boolean isSubjectClaimContainsNo;
        try {
            userId=Integer.parseInt(subClaimValue);
            isSubjectClaimContainsNo=true;
        } catch(NumberFormatException e) {
            isSubjectClaimContainsNo=false;
        }

        if(!isSubjectClaimContainsNo)
            return Optional.empty();

        boolean isSubjectClaimValueAnActualUserId=userRepository.isExistsById(userId);
        boolean isValidJwtAccessToken=isSubjectClaimValueAnActualUserId;

        if(!isValidJwtAccessToken)
            return  Optional.empty();

        return Optional.of(parsedToken);
    }

    public boolean isValidJwtRefreshToken(String stringToBeChecked) {
        JwtParser jwtRefreshTokenParser=Jwts.parser().requireIssuer(DOMAIN_NAME_ALONG_WITH_HTTP_SCHEME).requireAudience(DOMAIN_NAME_ALONG_WITH_HTTP_SCHEME).require("token_type","refresh").verifyWith(Keys.hmacShaKeyFor(hmacSHA256SecretKeyBytes)).build();
        Jws<Claims> parsedTokenObj;
        try {
            parsedTokenObj = jwtRefreshTokenParser.parseSignedClaims(stringToBeChecked);
        } catch(Exception e) {
            return false;
        }

        boolean isContainsTypKeyWithValueJwtInHeader=parsedTokenObj.getHeader().getType().equals("JWT");
        if(!isContainsTypKeyWithValueJwtInHeader) {
            return false;
        }

        boolean isContainsAlgKeyWithValueHS256InHeader=parsedTokenObj.getHeader().getAlgorithm().equals("HS256");
        if(!isContainsAlgKeyWithValueHS256InHeader) {
            return false;
        }

        String subClaimValue=parsedTokenObj.getPayload().getSubject();
        boolean isSubClaimNotPresent=subClaimValue==null;
        if(isSubClaimNotPresent)
            return false;

        int userId=0;
        boolean isSubjectClaimContainsNo;
        try {
            userId=Integer.parseInt(subClaimValue);
            isSubjectClaimContainsNo=true;
        } catch(NumberFormatException e) {
            isSubjectClaimContainsNo=false;
        }

        if(!isSubjectClaimContainsNo)
            return false;

        boolean isSubjectClaimValueAnActualUserId=userRepository.isExistsById(userId);
        if(!isSubjectClaimValueAnActualUserId)
            return false;

        String jtiClaimValue=parsedTokenObj.getPayload().getId();
        boolean isJtiClaimNotPresent=jtiClaimValue==null;
        if(isJtiClaimNotPresent)
            return false;

        UUID jtiClaimValueAsUUID=UUID.fromString(jtiClaimValue);
        Optional<JwtRefreshToken> optional=jwtRefreshTokenRepository.findByJtiClaimValueUUIDAndUserId(jtiClaimValueAsUUID,userId,false);
        boolean isStoredJwtRefreshTokenNotFound=optional.isEmpty();
        if(isStoredJwtRefreshTokenNotFound)
            return false;

        JwtRefreshToken storedJwtRefreshToken=optional.get();
        String storedJwtRefreshTokenHash=storedJwtRefreshToken.getRefreshTokenHash();
        String partiallyValidToken=stringToBeChecked;
        boolean isMatchesTheStoredJwtRefreshTokenHash=argon2IdPasswordEncoder.matches(partiallyValidToken,storedJwtRefreshTokenHash);
        if(!isMatchesTheStoredJwtRefreshTokenHash) {
            return false;
        }

        return true;
    }






}
