package com.awesometodo.dto;

import java.util.List;

public class TodoPageResponseDTO {
    private List<TodoResponseDTO> todos;
    private int totalTodos;

    public TodoPageResponseDTO(List<TodoResponseDTO> todos, int totalTodos) {
        this.todos = todos;
        this.totalTodos = totalTodos;
    }

    public List<TodoResponseDTO> getTodos() {
        return todos;
    }

    public void setTodos(List<TodoResponseDTO> todos) {
        this.todos = todos;
    }

    public int getTotalTodos() {
        return totalTodos;
    }

    public void setTotalTodos(int totalTodos) {
        this.totalTodos = totalTodos;
    }

    @Override
    public String toString() {
        return "TodoPageResponseDTO{" +
                "todos=" + todos +
                ", totalTodos=" + totalTodos +
                '}';
    }
}
