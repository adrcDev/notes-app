package com.awesometodo.controlleradvice;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private final static Logger logger= LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler({Exception.class})
    Map<String,String> globalExceptionHandler(HttpServletResponse response, Exception e) {
        logger.error("Unexpected unhandled exception was thrown:-",e);
        response.setStatus(500);
        HashMap<String,String> responseMessage=new HashMap<>();
        responseMessage.put("status","500");
        responseMessage.put("message","Internal server error");
        return responseMessage;
    }

    @ExceptionHandler({NoResourceFoundException.class})
    Map<String,String> noResourceFoundExceptionHandler(HttpServletResponse response) {
        response.setStatus(404);
        HashMap<String,String> responseMessage=new HashMap<>();
        responseMessage.put("status","404");
        responseMessage.put("message","Requested resource not found");
        return responseMessage;

    }

    @ExceptionHandler({HttpRequestMethodNotSupportedException.class })
    void httpRequestMethodNotSupportedExceptionHandler(HttpServletRequest request,HttpServletResponse response, HttpRequestMethodNotSupportedException e) {
        response.setStatus(405);
        logger.warn("{} was thrown while accessing {} {} endpoint:-",e.getClass().getSimpleName(),request.getMethod(),request.getRequestURI(),e);
    }

    @ExceptionHandler({HttpMessageNotReadableException.class})
    void httpMessageNotReadableExceptionHandler(HttpServletResponse response,HttpMessageNotReadableException e) {
        response.setStatus(400);
        logger.warn("The json deserialization of http request message's body failed:- ",e);
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    void methodArgumentNotValidExceptionHandler(HttpServletResponse response, MethodArgumentNotValidException e) {
        response.setStatus(400);
        logger.warn("The request message body's json which was deserialized to a java object was not considered valid by hibernate validator or the request message's query parameter values failed validation, so a MethodArgumentNotValidException was thrown by spring framework. The exception's message:-\n{}",e.getMessage());
    }

    @ExceptionHandler({MethodArgumentTypeMismatchException.class})
    void methodArgumentTypeMismatchExceptionHandler(HttpServletRequest request,HttpServletResponse response,MethodArgumentTypeMismatchException e) {
        response.setStatus(400);
        logger.warn("A {} was thrown while accessing {} {} endpoint:-",e.getClass().getSimpleName(),request.getMethod(),request.getRequestURI(),e);
    }

    @ExceptionHandler({ConstraintViolationException.class})
    void constraintViolationExceptionHandler(HttpServletRequest request,HttpServletResponse response, ConstraintViolationException e) {
        response.setStatus(400);
        logger.warn("A {} was thrown while accessing {} {} endpoint:-",e.getClass().getSimpleName(),request.getMethod(),request.getRequestURI(),e);
    }
}
