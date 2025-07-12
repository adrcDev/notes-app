package com.awesometodo;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

/* The commence() method is called by one of spring security's filter's if the SecurityContext does not contain an Authentication object. The SecurityContext object is present inside the SecurityContextHolder. If this method is called then it means the request message was not by an authenticated user so you can modify the http response message here which will be sent to the client. Usually you send a 401 unauthorized response message in such cases */
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {

        response.setStatus(401);
    }
}
