package com.awesometodo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    private final static Logger logger= LoggerFactory.getLogger(TestController.class);

    @GetMapping("/api/v1/hi")
    public String sayHi() {
        JwtAuthenticationToken authenticationObj=(JwtAuthenticationToken)SecurityContextHolder.getContext().getAuthentication();
        logger.debug("{}",authenticationObj.getName());
        logger.debug("{}",authenticationObj.getTokenAttributes());
        logger.debug("{}",authenticationObj.getToken().getSubject());
        return "Hi";
    }
}
