package com.awesometodo.controller;

import com.awesometodo.dto.TodoPageResponseDTO;
import com.awesometodo.dto.TodoQueryParamsDTO;
import com.awesometodo.dto.TodoPutRequestDTO;
import com.awesometodo.dto.TodoResponseDTO;
import com.awesometodo.exception.PatchTodoRequestValidationException;
import com.awesometodo.exception.TodoNotFoundForUserException;
import com.awesometodo.service.TodoService;
import com.awesometodo.springsecurity.SpringSecurityJwtFacade;
import com.awesometodo.validation.util.TodoPatchRequestValidator;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Map;

@RestController
@Validated
public class TodoV1Controller {
    private static final Logger logger= LoggerFactory.getLogger(TodoV1Controller.class);
    private TodoService todoService;
    private SpringSecurityJwtFacade springSecurityJwtFacade;

    public TodoV1Controller(TodoService todoService, SpringSecurityJwtFacade springSecurityJwtFacade) {
        this.todoService=todoService;
        this.springSecurityJwtFacade=springSecurityJwtFacade;
    }

    @GetMapping("/api/v1/todos")
    public TodoPageResponseDTO getMatchingTodos(@Valid TodoQueryParamsDTO todoQueryParamsDTO){
        logger.debug("GET /api/v1/todos endpoint started running");
        logger.debug("The received query parameters are {}", todoQueryParamsDTO);
        int userId= springSecurityJwtFacade.getUserIdFromJwtAccessToken();
        TodoPageResponseDTO todoPageResponseDTO=todoService.getMatchingTodosForUserId(userId,todoQueryParamsDTO);
        logger.debug("Retrieved todo page(limit={},offset={}) with {} todos and also the the total matching todos count which is {} for user with id of {}",todoQueryParamsDTO.getLimit(),todoQueryParamsDTO.getOffset(),todoPageResponseDTO.getTodos().size(),todoPageResponseDTO.getTotalTodos(),userId);
        logger.debug("GET /api/v1/todos endpoint finished running");
        return todoPageResponseDTO;
    }

    @GetMapping("/api/v1/todos/{id}")
    public TodoResponseDTO getTodo(@Min(value=1,message="The id path variable's(path segment) value should be a value >=1 in the URL /api/v1/todos/{id}") @PathVariable(name="id",required = true)int todoId) {
        logger.debug("GET /api/v1/todos/{id} endpoint started running");
        int userId= springSecurityJwtFacade.getUserIdFromJwtAccessToken();
        TodoResponseDTO todoResponseDTO=todoService.getTodoForUserId(todoId,userId);
        logger.debug("GET /api/v1/todos/{id} endpoint finished running");
        return todoResponseDTO;
    }

    @DeleteMapping("/api/v1/todos/{id}")
    public void deleteTodo(HttpServletResponse response,@Min(value=1,message="The id path variable's(path segment) value should be a value >=1 in the URL /api/v1/todos/{id}") @PathVariable(name="id",required = true)int todoId) {
        logger.debug("DELETE /api/v1/todos/{id} endpoint started running");
        int userId= springSecurityJwtFacade.getUserIdFromJwtAccessToken();
        todoService.deleteTodoByUserId(todoId,userId);
        response.setStatus(204);
        logger.debug("http response message's status code was set to 204");
        logger.debug("DELETE /api/v1/todos/{id} endpoint finished running");
    }

    @PostMapping("/api/v1/todos")
    public TodoResponseDTO postTodo(HttpServletResponse response) {
        logger.debug("POST /api/v1/todos endpoint started running");
        int userId= springSecurityJwtFacade.getUserIdFromJwtAccessToken();
        TodoResponseDTO createdTodoResponseDTO=todoService.createTodoForUserId(userId);
        response.setStatus(201);
        logger.debug("http response message's status code was set to 201");
        String locationResponseHeaderValue="/api/v1/todos/"+createdTodoResponseDTO.getId();
        response.setHeader("Location",locationResponseHeaderValue);
        logger.debug("Location header with value of {} was added to http response message's headers section",locationResponseHeaderValue);
        logger.debug("POST /api/v1/todos endpoint finished running");
        return createdTodoResponseDTO;
    }

    @PutMapping("/api/v1/todos/{id}")
    public TodoResponseDTO putTodo(@Min(value=1,message="The id path variable's(path segment) value should be a value >=1 in the URL /api/v1/todos/{id}") @PathVariable(name="id",required = true)int todoId, @Valid @RequestBody(required = false) TodoPutRequestDTO todoPutRequestDTO) {
        logger.debug("PUT /api/v1/todos/{id} endpoint started running");
        int userId= springSecurityJwtFacade.getUserIdFromJwtAccessToken();
        TodoResponseDTO fullUpdatedTodoResponseDTO=todoService.fullUpdateTodoForUserId(todoId,userId, todoPutRequestDTO);
        logger.debug("PUT /api/v1/todos/{id} endpoint finished running");
        return fullUpdatedTodoResponseDTO;
    }

    @PatchMapping("/api/v1/todos/{id}")
    public TodoResponseDTO patchTodo(@Min(value=1,message="The id path variable's(path segment) value should be a value >=1 in the URL /api/v1/todos/{id}") @PathVariable(name="id",required = true)int todoId, @RequestBody(required = true)JsonNode requestBodyJsonNode) {
        logger.debug("PATCH /api/v1/todos/{id} endpoint started running");
        Map<String,String> todoUpdateFieldsMap=
                TodoPatchRequestValidator.validateAndReturnMap(requestBodyJsonNode);
        int userId= springSecurityJwtFacade.getUserIdFromJwtAccessToken();
        TodoResponseDTO partialUpdatedTodoResponseDTO=todoService.partialUpdateTodoForUserId(todoId,userId,todoUpdateFieldsMap);
        logger.debug("PATCH /api/v1/todos/{id} endpoint finished running");
        return partialUpdatedTodoResponseDTO;
    }

    @ExceptionHandler({PatchTodoRequestValidationException.class})
    public void handlePatchTodoRequestValidationException(HttpServletResponse response,PatchTodoRequestValidationException e) {
        response.setStatus(400);
    }

    @ExceptionHandler({TodoNotFoundForUserException.class})
    public void handleTodoNotFoundForUserException(HttpServletResponse response,TodoNotFoundForUserException e) {
        response.setStatus(404);
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

    @ExceptionHandler({HttpMessageNotReadableException.class})
    public void handleHttpMessageNotReadableException(HttpServletResponse response,HttpMessageNotReadableException e) {
        response.setStatus(400);
        logger.warn("The json deserialization of http request message's body failed:- ",e);
    }




}
