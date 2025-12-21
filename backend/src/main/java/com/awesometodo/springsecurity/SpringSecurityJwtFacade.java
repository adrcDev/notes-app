package com.awesometodo.springsecurity;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
public class SpringSecurityJwtFacade {

    public int getUserIdFromJwtAccessToken() {
        JwtAuthenticationToken authenticationObj=(JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        String subjectClaimValue=authenticationObj.getToken().getSubject();
        return Integer.parseInt(subjectClaimValue);
    }
}
