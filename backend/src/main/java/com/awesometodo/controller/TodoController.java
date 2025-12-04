package com.awesometodo.controller;

import com.awesometodo.dto.TodoPageResponseDTO;
import com.awesometodo.dto.TodoQueryParamsDTO;
import com.awesometodo.exception.TodoNotFoundForUserException;
import com.awesometodo.service.TodoService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestController
@Validated
public class TodoController {
    private static final Logger logger= LoggerFactory.getLogger(TodoController.class);
    private TodoService todoService;

    public TodoController(TodoService todoService) {
        this.todoService=todoService;
    }

    @GetMapping("/api/v1/todos")
    public TodoPageResponseDTO getMatchingTodos(@Valid TodoQueryParamsDTO todoQueryParamsDTO){
        logger.debug("/api/v1/todos endpoint started running");
        logger.debug("The received query parameters are {}", todoQueryParamsDTO);
        int userId=getUserIdFromJwtAccessToken();
        TodoPageResponseDTO todoPageResponseDTO=todoService.getMatchingTodosForUserId(userId,todoQueryParamsDTO);
        logger.debug("/api/v1/todos endpoint finished running");
        return todoPageResponseDTO;
    }

    @DeleteMapping("/api/v1/todos/{id}")
    public void deleteTodo(HttpServletResponse response,@Min(value=1,message="The id path variable's(path segment) value should be a value >=1 in the URL /api/v1/todos/{id}") @PathVariable(name="id",required = true)int todoId) {
        int userId=getUserIdFromJwtAccessToken();
        todoService.deleteTodoByUserId(todoId,userId);
        response.setStatus(204);
    }

    @ExceptionHandler({TodoNotFoundForUserException.class})
    void todoNotFoundForUserExceptionHandler(HttpServletResponse response,TodoNotFoundForUserException e) {
        response.setStatus(404);
    }

    private int getUserIdFromJwtAccessToken() {
        JwtAuthenticationToken authenticationObj=(JwtAuthenticationToken)SecurityContextHolder.getContext().getAuthentication();
        String subjectClaimValue=authenticationObj.getToken().getSubject();
        return Integer.parseInt(subjectClaimValue);
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public void handleMethodArguemntNotValidException(HttpServletResponse response, MethodArgumentNotValidException e) {
        response.setStatus(400);
        logger.warn("The request message body's json which was deserialized to a java object was not considered valid by hibernate validator or the request message's query parameter values failed validation, so a MethodArgumentNotValidException was thrown by spring framework. The exception's message:-\n{}",e.getMessage());
    }

    @ExceptionHandler({ConstraintViolationException.class})
    public void handleConstraintViolationException(HttpServletRequest request,HttpServletResponse response, ConstraintViolationException e) {
        response.setStatus(400);
        logger.warn("A {} was thrown while accessing {} {} endpoint:-",e.getClass().getSimpleName(),request.getMethod(),request.getRequestURI(),e);
    }

    @ExceptionHandler({MethodArgumentTypeMismatchException.class})
    public void handleMethodArgumentTypeMismatchException(HttpServletRequest request,HttpServletResponse response,MethodArgumentTypeMismatchException e) {
        response.setStatus(400);
        logger.warn("A {} was thrown while accessing {} {} endpoint:-",e.getClass().getSimpleName(),request.getMethod(),request.getRequestURI(),e);
    }




}
