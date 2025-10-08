package com.awesometodo.controller;

import com.awesometodo.dto.*;
import com.awesometodo.exception.*;
import com.awesometodo.service.JwtService;
import com.awesometodo.service.UserForgotPasswordService;
import com.awesometodo.service.UserLoginService;
import com.awesometodo.service.UserSignupService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    UserLoginService userLoginService;
    JwtService jwtService;
    UserSignupService userSignupService;
    UserForgotPasswordService userForgotPasswordService;

    public AuthV1Controller(UserLoginService userLoginService, JwtService jwtService, UserSignupService userSignupService,UserForgotPasswordService userForgotPasswordService) {
        this.userLoginService = userLoginService;
        this.jwtService=jwtService;
        this.userSignupService=userSignupService;
        this.userForgotPasswordService=userForgotPasswordService;
    }

    @PostMapping("/auth/v1/login")
    public Map<String,String> login(@RequestBody @Valid LoginDataDTO loginDataDTO,HttpServletResponse response) {
        logger.debug("/auth/v1/login endpoint started running");
        JwtAuthTokensDTO jwtAuthTokensDTO= userLoginService.login(loginDataDTO);
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
        userSignupService.signupInitialization(signupDataDTO);
        logger.debug("/auth/v1/signup/init endpoint finished running");
    }

    @PostMapping("/auth/v1/signup/verify-otps")
    public void signupVerifyOtps(@RequestBody @Valid SignupOtpVerificationDataDTO signupOtpVerificationDataDTO) {
        logger.debug("/auth/v1/signup/verify-otps endpoint started running");
        userSignupService.signupVerifyOtps(signupOtpVerificationDataDTO);
        logger.debug("/auth/v1/signup/verify-otps endpoint finished running");
    }

    @PostMapping("/auth/v1/signup/resend-otps")
    public void signupResendOtps(@RequestBody @Valid SignupDataDTO signupDataDTO) {
        logger.debug("/auth/v1/signup/resend-otps endpoint started running");
        userSignupService.signupResendOtps(signupDataDTO);
        logger.debug("/auth/v1/signup/resend-otps endpoint finished running");
    }

    @ExceptionHandler({UserWithSameDetailsAlreadyExistsException.class, PendingSignupUserWithSameDetailsAlreadyExistsException.class,PendingSignupUserDoesntExistException.class,SignupOtpsExpiredException.class, SignupOtpMismatchException.class,SignupOtpsNotExpiredException.class})
    public void signupExceptionHandler(HttpServletResponse response) {
        response.setStatus(401);
    }

    @PostMapping("/auth/v1/forgot-password/init")
    public void forgotPasswordInit(@RequestBody @Valid ForgotPassswordDataDTO forgotPasswordDataDTO) {
        logger.debug("/auth/v1/forgot-password/init endpoint started running");
        userForgotPasswordService.forgotPasswordInitialisation(forgotPasswordDataDTO);
        logger.debug("/auth/v1/forgot-password/init endpoint finished running");
    }

    @PostMapping("/auth/v1/forgot-password/verify-otps")
    public Map<String,String> forgotPasswordVerifyOtps(@RequestBody @Valid ForgotPasswordOtpVerificationDataDTO forgotPasswordOtpVerificationDataDTO ) {
        logger.debug("/auth/v1/forgot-password/verify-otps endpoint started running");
        String passwordResetToken=userForgotPasswordService.forgotPasswordVerifyOtps(forgotPasswordOtpVerificationDataDTO);
        HashMap<String,String> responseBodyMessageMap=new HashMap<>();
        responseBodyMessageMap.put("password reset token",passwordResetToken);
        logger.debug("/auth/v1/forgot-password/verify-otps endpoint finished running");
        return responseBodyMessageMap;
    }

    @ExceptionHandler({UserDoesntExistException.class,UserDoesntHaveForgotPasswordOtpsException.class, ForgotPasswordOtpsExpiredException.class,ForgotPasswordOtpMismatchException.class})
    void forgotPasswordInitExceptionHandler(HttpServletResponse response) {
        response.setStatus(200);
    }

    @ExceptionHandler({ConcurrentOperationException.class})
    void forgotPasswordConcurrentOperationExceptionHandler(HttpServletResponse response,ConcurrentOperationException e) {
        response.setStatus(200);
        logger.warn(e.getMessage(),e);
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
