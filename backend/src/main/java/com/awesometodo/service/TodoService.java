package com.awesometodo.service;

import com.awesometodo.command.UpdateTodoCommand;
import com.awesometodo.dto.TodoPageResponseDTO;
import com.awesometodo.dto.TodoQueryParamsDTO;
import com.awesometodo.dto.TodoRequestDTO;
import com.awesometodo.dto.TodoResponseDTO;
import com.awesometodo.entity.Todo;
import com.awesometodo.entity.User;
import com.awesometodo.exception.TodoNotFoundForUserException;
import com.awesometodo.repository.TodoRepository;
import com.awesometodo.repository.UserRepository;
import com.awesometodo.repository.criteria.TodoQueryCriteria;
import com.awesometodo.repository.filter.TodoQueryFilter;
import com.awesometodo.util.EnumUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TodoService {
    private static final Logger logger= LoggerFactory.getLogger(TodoService.class);
    private TodoRepository todoRepository;
    private UserRepository userRepository;

    public TodoService(TodoRepository todoRepository,UserRepository userRepository) {
        this.todoRepository=todoRepository;
        this.userRepository=userRepository;
    }

    public TodoPageResponseDTO getMatchingTodosForUserId(int userId, TodoQueryParamsDTO todoQueryParamsDTO) {
        TodoQueryCriteria todoQueryCriteria=new TodoQueryCriteria(todoQueryParamsDTO.getTitleSearch(),todoQueryParamsDTO.getDescriptionSearch(),todoQueryParamsDTO.getContentSearch(),todoQueryParamsDTO.getPriority(),todoQueryParamsDTO.getStatus(),todoQueryParamsDTO.getDueDateFrom(),todoQueryParamsDTO.getDueDateTo(),todoQueryParamsDTO.getSortBy(),todoQueryParamsDTO.getSortOrder(),todoQueryParamsDTO.getLimit(),todoQueryParamsDTO.getOffset());
        List<Todo> todosMatchingQueryCriteria=todoRepository.findByUserIdAndQueryCriteria(userId,todoQueryCriteria);
        List<TodoResponseDTO> todoResponseDTOs=new ArrayList<>();
        for(int i=0;i<todosMatchingQueryCriteria.size();i++) {
            Todo todo=todosMatchingQueryCriteria.get(i);
            todoResponseDTOs.add(new TodoResponseDTO(todo.getId(),todo.getTitle(),todo.getDescription(),todo.getContentDelta(),todo.getDueDate(), EnumUtil.convertToSpaceSeparatedLowerCaseString(todo.getPriority()).get(),EnumUtil.convertToSpaceSeparatedLowerCaseString(todo.getStatus()).get(),todo.getCreatedAt(),todo.getUpdatedAt()));
        }

        TodoQueryFilter todoQueryFilter=new TodoQueryFilter(todoQueryParamsDTO.getTitleSearch(),todoQueryParamsDTO.getDescriptionSearch(),todoQueryParamsDTO.getContentSearch(),todoQueryParamsDTO.getPriority(),todoQueryParamsDTO.getStatus(),todoQueryParamsDTO.getDueDateFrom(),todoQueryParamsDTO.getDueDateTo());
        int matchingTodosCount=todoRepository.findCountByUserIdAndQueryFilter(userId,todoQueryFilter);
        return new TodoPageResponseDTO(todoResponseDTOs,matchingTodosCount);
    }

    @Transactional
    public void deleteTodoByUserId(int todoId,int userId) {
        boolean isTodoDeleted=todoRepository.deleteByIdAndUserId(todoId,userId);
        if(!isTodoDeleted) {
            throw new TodoNotFoundForUserException();
        }
    }

    @Transactional
    public TodoResponseDTO createTodoForUserId(int userId) {
        User user=userRepository.findById(userId).get();
        Todo newTodo=new Todo();
        newTodo.setUser(user);
        newTodo=todoRepository.insertAndReturn(newTodo);
        String priority=EnumUtil.convertToSpaceSeparatedLowerCaseString(newTodo.getPriority()).get();
        String status=EnumUtil.convertToSpaceSeparatedLowerCaseString(newTodo.getStatus()).get();
        TodoResponseDTO todoResponseDTO=new TodoResponseDTO(newTodo.getId(), newTodo.getTitle(), newTodo.getDescription(), newTodo.getContentDelta(), newTodo.getDueDate(),priority,status,newTodo.getCreatedAt(),newTodo.getUpdatedAt());
        return todoResponseDTO;
    }

    @Transactional
    public TodoResponseDTO fullUpdateTodoForUserId(int todoId, int userId, TodoRequestDTO todoRequestDTO) {
        UpdateTodoCommand updateTodoCommand=new UpdateTodoCommand(userId,todoId,todoRequestDTO.getTitle(),todoRequestDTO.getDescription(),todoRequestDTO.getContentText(),todoRequestDTO.getContentDelta(),todoRequestDTO.getDueDate(),todoRequestDTO.getPriority(),todoRequestDTO.getStatus());
        Optional<Todo> optionalTodo=todoRepository.fullUpdateAndReturn(updateTodoCommand);
        if(optionalTodo.isEmpty()) {
            throw new TodoNotFoundForUserException();
        }

        Todo fullUpdatedTodo=optionalTodo.get();
        String priority=EnumUtil.convertToSpaceSeparatedLowerCaseString(fullUpdatedTodo.getPriority()).get();
        String status=EnumUtil.convertToSpaceSeparatedLowerCaseString(fullUpdatedTodo.getStatus()).get();
        TodoResponseDTO fullUpdatedTodoResponseDTO=new TodoResponseDTO(fullUpdatedTodo.getId(),fullUpdatedTodo.getTitle(),fullUpdatedTodo.getDescription(), fullUpdatedTodo.getContentDelta(), fullUpdatedTodo.getDueDate(),priority,status,fullUpdatedTodo.getCreatedAt(),fullUpdatedTodo.getUpdatedAt());

        return fullUpdatedTodoResponseDTO;
    }
}
