package com.awesometodo.controlleradvice;

import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
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



}
