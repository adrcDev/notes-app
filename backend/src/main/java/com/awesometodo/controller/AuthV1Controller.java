package com.awesometodo.controller;

import com.awesometodo.dto.JwtAuthTokensDTO;
import com.awesometodo.dto.LoginDataDTO;
import com.awesometodo.entity.User;
import com.awesometodo.exception.InvalidCredentialsException;
import com.awesometodo.service.JwtService;
import com.awesometodo.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class AuthV1Controller {
    UserService userService;
    JwtService jwtService;

    public void AuthV1Controller(UserService userService) {
        this.userService=userService;
        this.jwtService=jwtService;
    }

    @PostMapping("/auth/v1/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginDataDTO loginDataDTO) {
        System.out.println("login endpoint ran");
        JwtAuthTokensDTO jwtAuthTokensDTO=userService.login(loginDataDTO);

        return ResponseEntity.ok().build();  //placeholder

    }


    @ExceptionHandler({MethodArgumentNotValidException.class,HttpMessageNotReadableException.class})
    public void handleValidationAndInvalidRequestExceptions(HttpServletResponse response) {
        response.setStatus(401);
    }

    @ExceptionHandler({InvalidCredentialsException.class})
    public void handleInvalidLoginCredentialsException(HttpServletResponse response) {
        response.setStatus(401);
    }



}
