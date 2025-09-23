package com.awesometodo.controller;

import com.awesometodo.dto.JwtAuthTokensDTO;
import com.awesometodo.dto.LoginDataDTO;
import com.awesometodo.dto.SignupDataDTO;
import com.awesometodo.entity.User;
import com.awesometodo.exception.InvalidCredentialsException;
import com.awesometodo.exception.PendingSignupUserWithSameDetailsAlreadyExistsException;
import com.awesometodo.exception.UserWithSameDetailsAlreadyExistsException;
import com.awesometodo.service.JwtService;
import com.awesometodo.service.UserService;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
/* Only added for development */
@CrossOrigin(allowCredentials = "true",origins ={"http://localhost:8081"})
public class AuthV1Controller {
    private static final Logger logger= LoggerFactory.getLogger(AuthV1Controller.class);
    private static final String JWT_REFRESH_TOKEN_COOKIE_NAME="jwt_refresh_token";

    UserService userService;
    JwtService jwtService;

    public AuthV1Controller(UserService userService,JwtService jwtService) {
        this.userService=userService;
        this.jwtService=jwtService;
    }

    @PostMapping("/auth/v1/login")
    public Map<String,String> login(@RequestBody @Valid LoginDataDTO loginDataDTO,HttpServletResponse response) {
        logger.debug("/auth/v1/login endpoint started running");
        JwtAuthTokensDTO jwtAuthTokensDTO=userService.login(loginDataDTO);
        String jwtRefreshToken=jwtAuthTokensDTO.getJwtRefreshToken();
        addJwtRefreshTokenAsCookie(response,jwtRefreshToken);
        logger.debug("jwt refresh token was added as a cookie in the http response message");

        HashMap<String,String> responseBodyMessage=new HashMap<>();
        String jwtAccessToken=jwtAuthTokensDTO.getJwtAccessToken();
        responseBodyMessage.put("jwt access token",jwtAccessToken);
        logger.debug("jwt access token was added as json in the http response message's body");
        logger.debug("/auth/v1/login endpoint finished running");
        return responseBodyMessage;
    }

    private void addJwtRefreshTokenAsCookie(HttpServletResponse response,String jwtRefreshToken) {
        Cookie jwtRefreshTokenCookie=new Cookie(JWT_REFRESH_TOKEN_COOKIE_NAME,jwtRefreshToken);
        jwtRefreshTokenCookie.setHttpOnly(true);
        jwtRefreshTokenCookie.setSecure(true);
        jwtRefreshTokenCookie.setPath("/auth/v1");
        int maxAgeCookieAttributeValue=jwtService.getRemainingTokenLifeTimeInSeconds(jwtRefreshToken);
        jwtRefreshTokenCookie.setMaxAge(maxAgeCookieAttributeValue);
        jwtRefreshTokenCookie.setAttribute("SameSite", "Strict");
        response.addCookie(jwtRefreshTokenCookie);
    }

    @ExceptionHandler({InvalidCredentialsException.class})
    public void handleInvalidLoginCredentialsException(HttpServletResponse response) {
        response.setStatus(401);
    }

    @PostMapping("/auth/v1/signup/init")
    public void signupInit(@RequestBody @Valid SignupDataDTO signupDataDTO) {
        logger.debug("/auth/v1/signup/init endpoint started running");
        userService.signupInitialization(signupDataDTO);
        logger.debug("/auth/v1/signup/init endpoint finished running");
    }

    @ExceptionHandler({UserWithSameDetailsAlreadyExistsException.class, PendingSignupUserWithSameDetailsAlreadyExistsException.class})
    public void signupInitExceptionHandler(HttpServletResponse response) {
        response.setStatus(401);
    }



    @ExceptionHandler({MethodArgumentNotValidException.class})
    public void handleMethodArguemntNotValidException(HttpServletResponse response,MethodArgumentNotValidException e) {
        response.setStatus(401);
        logger.warn("The request message body's json which was deserialized to a java object was not considered valid by hibernate validator, so a MethodArgumentNotValidException was thrown by spring framework. The exception's message:-\n{}",e.getMessage());

    }

    @ExceptionHandler({HttpMessageNotReadableException.class})
    public void handleValidationAndInvalidRequestExceptions(HttpServletResponse response,HttpMessageNotReadableException e) {
        response.setStatus(401);
        logger.warn("The request message's body could not be deserialized to the expected java object. The exception's message:-\n{} ",e.getMessage());
    }





}
