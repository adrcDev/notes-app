package com.awesometodo;

import com.awesometodo.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.oauth2.jwt.Jwt;


import java.io.IOException;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService=jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorizationReqHeaderValue=request.getHeader("Authorization");
        boolean isAuthorizationReqHeaderNotPresent=authorizationReqHeaderValue==null;
        if(isAuthorizationReqHeaderNotPresent) {
            filterChain.doFilter(request, response);
            return;
        }

        authorizationReqHeaderValue=authorizationReqHeaderValue.trim();
        boolean isNotBearerAuthenticationScheme=!authorizationReqHeaderValue.substring(0,6).equals("Bearer");
        if(isNotBearerAuthenticationScheme) {
            filterChain.doFilter(request, response);
            return;
        }

        String stringToBeChecked=authorizationReqHeaderValue.substring(7).trim();
        Optional<Jws<Claims>> optional=jwtService.checkIfStringIsValidJwtAccessTokenAndReturnJwsObj(stringToBeChecked);
        boolean isNotValidJwtAccessToken=optional.isEmpty();
        if(isNotValidJwtAccessToken) {
            filterChain.doFilter(request,response);
            return;
        }

        Jws<Claims> validatedJwtAccessTokenObj=optional.get();
        String validatedJwtAcessToken=stringToBeChecked;
        markThisRequestAsAuthenticatedByFillingSecurityContextHolder(validatedJwtAccessTokenObj,validatedJwtAcessToken);

        filterChain.doFilter(request,response);
    }

    private void markThisRequestAsAuthenticatedByFillingSecurityContextHolder(Jws<Claims> validatedJwtAccessTokenObj,String validatedJwtAccessToken) {
        HashMap<String, Object> headersMap=new HashMap<>();
        HashMap<String, Object> claimsMap=new HashMap<>();
        headersMap.put("typ",validatedJwtAccessTokenObj.getHeader().getType());
        headersMap.put("alg",validatedJwtAccessTokenObj.getHeader().getAlgorithm());
        claimsMap.put("iss",validatedJwtAccessTokenObj.getPayload().getIssuer());
        claimsMap.put("sub",validatedJwtAccessTokenObj.getPayload().getSubject());
        claimsMap.put("aud",validatedJwtAccessTokenObj.getPayload().getAudience());
        Instant iatClaimValueAsInstant=validatedJwtAccessTokenObj.getPayload().getIssuedAt().toInstant();
        Instant expClaimValueAsInstant=validatedJwtAccessTokenObj.getPayload().getExpiration().toInstant();
        Authentication authentication= new JwtAuthenticationToken(new Jwt(validatedJwtAccessToken,iatClaimValueAsInstant,expClaimValueAsInstant,headersMap,claimsMap), List.of());
        SecurityContext securityContext=SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);
    }
}
