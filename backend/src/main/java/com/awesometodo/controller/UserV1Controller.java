package com.awesometodo.controller;

import com.awesometodo.dto.UserDetailsResponseDTO;
import com.awesometodo.service.UserDetailsService;
import com.awesometodo.springsecurity.SpringSecurityJwtFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserV1Controller {
    private static Logger logger= LoggerFactory.getLogger(UserV1Controller.class);
    private SpringSecurityJwtFacade springSecurityJwtFacade;
    private UserDetailsService userDetailsService;

    public UserV1Controller(UserDetailsService userDetailsService,SpringSecurityJwtFacade springSecurityJwtFacade) {
        this.userDetailsService=userDetailsService;
        this.springSecurityJwtFacade=springSecurityJwtFacade;
    }

    @GetMapping("/api/v1/users/me")
    public UserDetailsResponseDTO getUserDetailsForAuthenticatedUser() {
        int userId=springSecurityJwtFacade.getUserIdFromJwtAccessToken();
        UserDetailsResponseDTO userDetailsResponseDTO=userDetailsService.getUserDetails(userId);
        return userDetailsResponseDTO;
    }
}
