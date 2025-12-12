package com.awesometodo.command;
import java.util.Map;
public class PartialUpdateTodoCommand {
    private int userId;
    private int todoId;
    private Map<String,String> todoUpdateFieldsMap;

    public PartialUpdateTodoCommand(int userId, int todoId, Map<String, String> todoUpdateFieldsMap) {
        this.userId = userId;
        this.todoId = todoId;
        this.todoUpdateFieldsMap = todoUpdateFieldsMap;
    }

    public int getUserId() {
        return userId;
    }

    public int getTodoId() {
        return todoId;
    }

    public Map<String, String> getTodoUpdateFieldsMap() {
        return todoUpdateFieldsMap;
    }

    @Override
    public String toString() {
        return "PartialUpdateTodoCommand{" +
                "userId=" + userId +
                ", todoId=" + todoId +
                ", todoUpdateFieldsMap=" + todoUpdateFieldsMap +
                '}';
    }
}
