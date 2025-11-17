package com.awesometodo.controller;

import com.awesometodo.dto.TodoQueryParamsDTO;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
/* Only added for development */
@CrossOrigin(allowCredentials = "true",origins ={"http://localhost:8081"})
public class TodoController {
    private static final Logger logger= LoggerFactory.getLogger(TodoController.class);

    @GetMapping("/api/v1/todos")
    public Map<String,String> getTodos(@Valid TodoQueryParamsDTO todoQueryParamsDTO){
        logger.debug("/api/v1/todos endpoint started running");
        logger.debug("The received query parameters are {}", todoQueryParamsDTO);

        logger.debug("/api/v1/todos endpoint finished running");
        //placeholder
        return Map.of("message","hi");
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public void handleMethodArguemntNotValidException(HttpServletResponse response, MethodArgumentNotValidException e) {
        response.setStatus(400);
        logger.warn("The request message body's json which was deserialized to a java object was not considered valid by hibernate validator or the request message's query parameter values failed validation, so a MethodArgumentNotValidException was thrown by spring framework. The exception's message:-\n{}",e.getMessage());
    }
}
