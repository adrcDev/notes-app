package com.awesometodo.controller;

import com.awesometodo.dto.*;
import com.awesometodo.exception.*;
import com.awesometodo.service.*;
import com.awesometodo.springsecurity.SpringSecurityJwtFacade;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
public class AuthV1Controller {
    private static final Logger logger= LoggerFactory.getLogger(AuthV1Controller.class);
    private static final String JWT_REFRESH_TOKEN_COOKIE_NAME="jwt_refresh_token";
    private static final String JWT_ACCESS_TOKEN_JSON_KEY_NAME="jwt access token";

    private UserLoginService userLoginService;
    private JwtService jwtService;
    private UserSignupService userSignupService;
    private UserForgotPasswordService userForgotPasswordService;
    private UserJwtRefreshService userJwtRefreshService;
    private UserLogoutService userLogoutService;
    private SpringSecurityJwtFacade springSecurityJwtFacade;

    public AuthV1Controller(UserLoginService userLoginService, JwtService jwtService, UserSignupService userSignupService, UserForgotPasswordService userForgotPasswordService, UserJwtRefreshService userJwtRefreshService,UserLogoutService userLogoutService,SpringSecurityJwtFacade springSecurityJwtFacade) {
        this.userLoginService = userLoginService;
        this.jwtService=jwtService;
        this.userSignupService=userSignupService;
        this.userForgotPasswordService=userForgotPasswordService;
        this.userJwtRefreshService = userJwtRefreshService;
        this.userLogoutService=userLogoutService;
        this.springSecurityJwtFacade=springSecurityJwtFacade;
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
        responseBodyMessage.put(JWT_ACCESS_TOKEN_JSON_KEY_NAME,jwtAccessToken);
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
//        jwtRefreshTokenCookie.setAttribute("SameSite", "Strict");
        //added only for development
        jwtRefreshTokenCookie.setAttribute("SameSite", "None");
        response.addCookie(jwtRefreshTokenCookie);
    }

    @ExceptionHandler({InvalidCredentialsException.class})
    public void invalidLoginCredentialsExceptionHandler(HttpServletResponse response) {
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

    @ExceptionHandler({UserWithSameDetailsAlreadyExistsException.class,PendingSignupUserWithSameDetailsAlreadyExistsException.class})
    public void handleSameUserDetailsAlreadyExistsException(HttpServletResponse response) {
        response.setStatus(409);
    }

    @ExceptionHandler({PendingSignupUserDoesntExistException.class})
    public void handlePendingSignupUserDoesntExistException(HttpServletResponse response) {
        response.setStatus(400);
    }

    @ExceptionHandler({SignupOtpsExpiredException.class, SignupOtpMismatchException.class,SignupOtpsNotExpiredException.class})
    public void handleSignupOtpsRelatedExceptions(HttpServletResponse response) {
        response.setStatus(400);
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
    void forgotPasswordInitAndVerifyOtpsExceptionHandler(HttpServletResponse response) {
        response.setStatus(200);
    }

    @ExceptionHandler({ConcurrentOperationException.class})
    void forgotPasswordInitAndVerifyOtpsConcurrentOperationExceptionHandler(HttpServletResponse response,ConcurrentOperationException e) {
        response.setStatus(200);
        logger.warn(e.getMessage(),e);
    }

    @PostMapping("/auth/v1/forgot-password/reset-password")
    public void forgotPasswordResetPassword(@RequestBody @Valid ForgotPasswordResetDataDTO forgotPasswordResetDataDTO) {
        logger.debug("/auth/v1/forgot-password/reset-password endpoint started running");
        userForgotPasswordService.forgotPasswordResetPassword(forgotPasswordResetDataDTO);
        logger.debug("/auth/v1/forgot-password/reset-password endpoint finished running");
    }

    @ExceptionHandler({PasswordResetTokenDoesntExistException.class, PasswordResetTokenExpiredException.class})
    public void forgotPasswordResetPasswordExceptionHandler(HttpServletResponse response) {
        response.setStatus(401);
    }


    @PostMapping("/auth/v1/refresh")
    public Map<String,String> refresh(HttpServletRequest request,HttpServletResponse response) {
        logger.debug("/auth/v1/refresh endpoint started running");
        logger.debug("Jwt authentication refresh process started. Checking if the http request message contains any cookies in the Cookie request header");
        Cookie[] cookies=request.getCookies();
        boolean isRequestMessageNotContainCookies=cookies==null;
        if(isRequestMessageNotContainCookies) {
            String exceptionMessage="The http request message didn't contain any cookies within Cookie header";
            logger.warn("{}. Aborting the jwt authentication refresh process.",exceptionMessage);
            throw new HttpRequestCookiesException(exceptionMessage);
        }

        logger.debug("The http request message contains atleast one cookie within the Cookie request header. Checking if one of the cookie's name matches the name that is expected for carrying the jwt refresh token value and getting its value");
        String cookieValue=null;
        for(Cookie cookie:cookies) {
            if(cookie.getName().equals(JWT_REFRESH_TOKEN_COOKIE_NAME))
                cookieValue=cookie.getValue().trim();
        }

        if(cookieValue==null) {
            String exceptionMessage="The http request message's Cookie header did not contain any cookie whose name matches the name that is expected to carry the jwt refresh token";
            logger.warn("{} ,Aborting the jwt authentication refresh process.",exceptionMessage);
            throw new HttpRequestCookiesException(exceptionMessage);
        }

        logger.debug("The http request message's Cookie header contained a cookie that matched the name that is expected to carry the jwt refresh token and its value has been extracted");
        JwtAuthTokensDTO jwtAuthTokensDTO= userJwtRefreshService.refresh(cookieValue);
        String newJwtRefreshToken=jwtAuthTokensDTO.getJwtRefreshToken();
        String jwtAccessToken=jwtAuthTokensDTO.getJwtAccessToken();
        addJwtRefreshTokenAsCookie(response,newJwtRefreshToken);
        logger.debug("The new stored jwt refresh token associated to user with id:{} was added as a cookie along with cookie attributes to the Set-Cookie response header of the http response message",jwtService.parseSubjectClaimValue(newJwtRefreshToken));

        Map<String,String> responseBodyMessage=new HashMap<>();
        responseBodyMessage.put(JWT_ACCESS_TOKEN_JSON_KEY_NAME,jwtAccessToken);
        logger.debug("The generated jwt access token associated to user with id:{} was put in the response message's body",jwtService.parseSubjectClaimValue(jwtAccessToken));
        logger.info("The jwt authentication refresh process completed successfully. New jwt refresh token was generated,stored and put in the Set-Cookie response header along with cookie attributes. A jwt access token was generated and sent in the response message's body");
        logger.debug("/auth/v1/refresh endpoint finished running");
        return responseBodyMessage;
    }



    @PostMapping("/auth/v1/logout")
    void logout(HttpServletRequest request,HttpServletResponse response) {
        response.setStatus(204);
        logger.debug("/auth/v1/logout endpoint started running");
        int userId= springSecurityJwtFacade.getUserIdFromJwtAccessToken();
        logger.debug("Logout process attempted by user with id:{}",userId);
        logger.debug("Checking if the http request message contains any cookies in the Cookie request header");
        Cookie[] cookies=request.getCookies();
        boolean isRequestMessageNotContainCookies=cookies==null;
        if(isRequestMessageNotContainCookies) {
            String exceptionMessage="The http request message didn't contain any cookies within Cookie header";
            logger.warn("{}. Aborting the logout process.",exceptionMessage);
            throw new HttpRequestCookiesException(exceptionMessage);
        }

        logger.debug("The http request message contains atleast one cookie within the Cookie request header. Checking if one of the cookie's name matches the name that is expected for carrying the jwt refresh token value and getting its value");
        String cookieValue=null;
        for(Cookie cookie:cookies) {
            if(cookie.getName().equals(JWT_REFRESH_TOKEN_COOKIE_NAME))
                cookieValue=cookie.getValue().trim();
        }

        if(cookieValue==null) {
            String exceptionMessage="The http request message's Cookie header did not contain any cookie whose name matches the name that is expected to carry the jwt refresh token";
            logger.warn("{} ,Aborting the logout process.",exceptionMessage);
            throw new HttpRequestCookiesException(exceptionMessage);
        }

        logger.debug("The http request message's Cookie header contained a cookie that matched the name that is expected to carry the jwt refresh token and its value has been extracted");
        userLogoutService.logout(cookieValue);
        addInstructionToRemoveJwtRefreshTokenCookie(response);
        logger.debug("The jwt refresh token cookie was set in the http response message's Set-Cookie header with Max-Age cookie attribute having value of 0 seconds in order to make the browser delete the stored jwt refresh token cookie");
        logger.info("The logout process has successfully completed for user with id:{}",userId);
        logger.debug("/auth/v1/logout endpoint finished running");
    }

    private void addInstructionToRemoveJwtRefreshTokenCookie(HttpServletResponse response) {
        Cookie jwtRefreshTokenCookie=new Cookie(JWT_REFRESH_TOKEN_COOKIE_NAME,"");
        jwtRefreshTokenCookie.setHttpOnly(true);
        jwtRefreshTokenCookie.setSecure(true);
        jwtRefreshTokenCookie.setPath("/auth/v1");
        jwtRefreshTokenCookie.setMaxAge(0);
//        jwtRefreshTokenCookie.setAttribute("SameSite", "Strict");
        //Added only for development
        jwtRefreshTokenCookie.setAttribute("SameSite", "None");
        response.addCookie(jwtRefreshTokenCookie);
    }

    @ExceptionHandler({HttpRequestCookiesException.class,InvalidJwtRefreshTokenException.class,JwtRefreshTokenStatusNotValidException.class,JwtRefreshTokenStatusCompromisedException.class, InvalidatedStatusJwtRefreshTokenNoExtensionPeriodException.class, InvalidatedStatusJwtRefreshTokenExtensionPeriodExpiredException.class})
    void refreshAndLogoutExceptionHandler(HttpServletResponse response) {
        response.setStatus(401);
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public void handleMethodArguemntNotValidException(HttpServletResponse response,MethodArgumentNotValidException e) {
        response.setStatus(400);
        logger.warn("The request message body's json which was deserialized to a java object was not considered valid by hibernate validator, so a MethodArgumentNotValidException was thrown by spring framework. The exception's message:-\n{}",e.getMessage());

    }

    @ExceptionHandler({HttpMessageNotReadableException.class})
    public void handleHttpMessageNotReadableExceptionException(HttpServletResponse response,HttpMessageNotReadableException e) {
        response.setStatus(400);
        logger.warn("The request message's body could not be deserialized to the expected java object. The exception's message:-\n{} ",e.getMessage());
    }





}
