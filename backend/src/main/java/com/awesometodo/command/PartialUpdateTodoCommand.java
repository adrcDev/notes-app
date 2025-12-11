package com.awesometodo.command;
import java.util.Map;
public class PartialUpdateTodoCommand {
    private int userId;
    private int todoId;
    private Map<String,String> updateTodoFieldsMap;

    public PartialUpdateTodoCommand(int userId, int todoId, Map<String, String> updateTodoFieldsMap) {
        this.userId = userId;
        this.todoId = todoId;
        this.updateTodoFieldsMap = updateTodoFieldsMap;
    }

    public int getUserId() {
        return userId;
    }

    public int getTodoId() {
        return todoId;
    }

    public Map<String, String> getUpdateTodoFieldsMap() {
        return updateTodoFieldsMap;
    }

    @Override
    public String toString() {
        return "PartialUpdateTodoCommand{" +
                "userId=" + userId +
                ", todoId=" + todoId +
                ", updateTodoFieldsMap=" + updateTodoFieldsMap +
                '}';
    }
}
