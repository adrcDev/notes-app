package com.awesometodo.service;

import com.awesometodo.repository.UserRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;

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

    public JwtService(UserRepository userRepository) {
        this.userRepository=userRepository;
    }

    public String generateJwtAccessToken(int userIdToUseAsSubjectClaimValue) {
        String jwtAccessToken= Jwts.builder().header().type("JWT")
                .and().claims().issuer(DOMAIN_NAME_ALONG_WITH_HTTP_SCHEME).subject(String.valueOf(userIdToUseAsSubjectClaimValue)).add("aud",DOMAIN_NAME_ALONG_WITH_HTTP_SCHEME).expiration(new Date(System.currentTimeMillis()+JWT_ACCESS_TOKEN_EXPIRY_TIME_IN_MILLIS)).issuedAt(new Date()).add("token_type","access")
                .and().signWith(Keys.hmacShaKeyFor(hmacSHA256SecretKeyBytes)).compact();
        return jwtAccessToken;
    }


    public String generateJwtRefreshToken(int userIdToUseAsSubjectClaimValue) {
        String jwtRefreshToken=Jwts.builder().header().type("JWT").and().claims().issuer(DOMAIN_NAME_ALONG_WITH_HTTP_SCHEME).subject(String.valueOf(userIdToUseAsSubjectClaimValue)).add("aud",DOMAIN_NAME_ALONG_WITH_HTTP_SCHEME).expiration(new Date(System.currentTimeMillis()+JWT_REFRESH_TOKEN_EXPIRY_TIME_IN_MILLIS)).issuedAt(new Date()).add("token_type","refresh").and().signWith(Keys.hmacShaKeyFor(hmacSHA256SecretKeyBytes)).compact();
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






}
