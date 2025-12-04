package com.awesometodo.service;

import com.awesometodo.dto.TodoPageResponseDTO;
import com.awesometodo.dto.TodoQueryParamsDTO;
import com.awesometodo.dto.TodoResponseDTO;
import com.awesometodo.entity.Todo;
import com.awesometodo.exception.TodoNotFoundForUserException;
import com.awesometodo.repository.TodoRepository;
import com.awesometodo.repository.criteria.TodoQueryCriteria;
import com.awesometodo.repository.filter.TodoQueryFilter;
import com.awesometodo.util.EnumUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class TodoService {
    private static final Logger logger= LoggerFactory.getLogger(TodoService.class);
    private TodoRepository todoRepository;

    public TodoService(TodoRepository todoRepository) {
        this.todoRepository=todoRepository;
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
        boolean isTodoExistsForUser=todoRepository.isExistsByIdAndUserId(todoId,userId);
        if(!isTodoExistsForUser) {
            throw new TodoNotFoundForUserException();
        }
        todoRepository.deleteByIdAndUserId(todoId,userId);
    }
}
