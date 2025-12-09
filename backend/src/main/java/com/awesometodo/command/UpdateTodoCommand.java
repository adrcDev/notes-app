package com.awesometodo.command;


import com.awesometodo.validation.constraint.NotEmptyString;
import com.awesometodo.validation.constraint.ValidQuillDelta;
import com.awesometodo.validation.constraint.ValidTodoPriority;
import com.awesometodo.validation.constraint.ValidTodoStatus;

import java.time.LocalDate;

public class UpdateTodoCommand {
    private int userId;
    private int todoId;
    private String title;
    private String description;
    private String contentText;
    private String contentDelta;
    private LocalDate dueDate;
    private String priority;
    private String status;

    public UpdateTodoCommand(int userId, int todoId, String title, String description, String contentText, String contentDelta, LocalDate dueDate, String priority, String status) {
        this.userId = userId;
        this.todoId = todoId;
        this.title = title;
        this.description = description;
        this.contentText = contentText;
        this.contentDelta = contentDelta;
        this.dueDate = dueDate;
        this.priority = priority;
        this.status = status;
    }

    public int getUserId() {
        return userId;
    }

    public int getTodoId() {
        return todoId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getContentText() {
        return contentText;
    }

    public String getContentDelta() {
        return contentDelta;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public String getPriority() {
        return priority;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "UpdateTodoCommand{" +
                "userId=" + userId +
                ", todoId=" + todoId +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", contentText='" + contentText + '\'' +
                ", contentDelta='" + contentDelta + '\'' +
                ", dueDate=" + dueDate +
                ", priority='" + priority + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
