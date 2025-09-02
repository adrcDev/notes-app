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
    private static final String JWT_REFRESH_TOKEN_COOKIE_NAME="jwt_refresh_token";

    UserService userService;
    JwtService jwtService;

    public AuthV1Controller(UserService userService,JwtService jwtService) {
        this.userService=userService;
        this.jwtService=jwtService;
    }

    @PostMapping("/auth/v1/login")
    public Map<String,String> login(@RequestBody @Valid LoginDataDTO loginDataDTO,HttpServletResponse response) {
//        System.out.println("login endpoint ran");
        JwtAuthTokensDTO jwtAuthTokensDTO=userService.login(loginDataDTO);
        String jwtRefreshToken=jwtAuthTokensDTO.getJwtRefreshToken();
        addJwtRefreshTokenAsCookie(response,jwtRefreshToken);

        HashMap<String,String> responseBodyMessage=new HashMap<>();
        String jwtAccessToken=jwtAuthTokensDTO.getJwtAccessToken();
        responseBodyMessage.put("jwt access token",jwtAccessToken);
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
        System.out.println(" signup init endpoint ran");
        userService.signupInitialization(signupDataDTO);

    }

    @ExceptionHandler({UserWithSameDetailsAlreadyExistsException.class, PendingSignupUserWithSameDetailsAlreadyExistsException.class})
    public void signupInitExceptionHandler(HttpServletResponse response) {
        response.setStatus(401);
    }



    @ExceptionHandler({MethodArgumentNotValidException.class,HttpMessageNotReadableException.class})
    public void handleValidationAndInvalidRequestExceptions(HttpServletResponse response,Exception e) {
        response.setStatus(401);
    }





}
