package com.awesometodo;

import com.awesometodo.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static Logger logger=LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService=jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        logger.debug("Http request message arrived at JwtAuthenticationFilter. Jwt authentication process started. Checking whether the request message has the Authorization http request header");
        String authorizationReqHeaderValue=request.getHeader("Authorization");
        boolean isAuthorizationReqHeaderNotPresent=authorizationReqHeaderValue==null;
        if(isAuthorizationReqHeaderNotPresent) {
            logger.warn("The http request message does not contain the Authorization http request header. The Jwt authentication process failed and thus the request message won't be allowed to access the requested protected resource");
            filterChain.doFilter(request, response);
            return;
        }

        logger.debug("The http request message has a Authorization request header. Checking whether the Authorization request header's value uses the 'Bearer' authentication scheme");
        String noBearerAuthSchemeWarnMessage="The http request message's Authorization request header value does not use the 'Bearer' authentication scheme. The Jwt authentication process failed and thus the request message won't be allowed to access the requested protected resource";
        authorizationReqHeaderValue=authorizationReqHeaderValue.trim();
        boolean isNotBearerAuthenticationScheme=authorizationReqHeaderValue.length()<6;
        if(isNotBearerAuthenticationScheme) {
            logger.warn(noBearerAuthSchemeWarnMessage);
            filterChain.doFilter(request, response);
            return;
        }

        isNotBearerAuthenticationScheme=!authorizationReqHeaderValue.substring(0,6).equals("Bearer");
        if(isNotBearerAuthenticationScheme) {
            logger.warn(noBearerAuthSchemeWarnMessage);
            filterChain.doFilter(request, response);
            return;
        }

        logger.debug("The http request message's Authorization request header value uses the 'Bearer' authentication scheme. Checking whether the value following the 'Bearer' string is a valid jwt access token");
        String notValidJwtAccessTokenWarnMesssage="The http request message's Authorization request header did not contain a valid jwt access token. The Jwt authentication process failed and thus the request message won't be allowed to access the requested protected resource";
        boolean isNotValidJwtAccessToken=authorizationReqHeaderValue.length()<8;
        if(isNotValidJwtAccessToken) {
            logger.warn(notValidJwtAccessTokenWarnMesssage);
            filterChain.doFilter(request,response);
            return;
        }

        String stringToBeChecked=authorizationReqHeaderValue.substring(7).trim();
        Optional<Jws<Claims>> optional=jwtService.checkIfStringIsValidJwtAccessTokenAndReturnJwsObj(stringToBeChecked);
        isNotValidJwtAccessToken=optional.isEmpty();
        if(isNotValidJwtAccessToken) {
            logger.warn(notValidJwtAccessTokenWarnMesssage);
            filterChain.doFilter(request,response);
            return;
        }

        logger.debug("The http request message's Authorization request header contained a valid jwt access token. The process to mark the http request message as authenticated begins");
        Jws<Claims> validatedJwtAccessTokenObj=optional.get();
        String validatedJwtAcessToken=stringToBeChecked;
        markThisRequestAsAuthenticatedByFillingSecurityContextHolder(validatedJwtAccessTokenObj,validatedJwtAcessToken);

        logger.info("The jwt authentication process is successfully completed by the http request message and thus it is allowed to access the protected requested resource");
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
        logger.debug("The SecurityContextHolder was filled with a SecurityContext object which in turn was filled with a subtype of Authentication which is JwtAuthenticationToken in this case. The http request message is thus marked as authenticated and allowed to access the protected requested resource");
    }
}
