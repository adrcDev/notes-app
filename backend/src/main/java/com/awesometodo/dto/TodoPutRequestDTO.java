package com.awesometodo.dto;

import com.awesometodo.validation.constraint.*;

import java.time.LocalDate;

@ContentTextAndDeltaPair
public class TodoPutRequestDTO {
    private String title;
    private String description;
    private String contentText;

    @NotEmptyString(message = "Value for contentDelta json key cannot be an empty string and must contain a valid quill delta")
    @ValidQuillDelta(message = "Value for contentDelta json key is not in quill delta format")
    private String contentDelta;

    private LocalDate dueDate;

    @ValidTodoPriority(message="Value for priority json key should be one of the valid todo priority values")
    private String priority;

    @ValidTodoStatus(message="Value for status json key should be one of the valid todo status values")
    private String status;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContentText() {
        return contentText;
    }

    public void setContentText(String contentText) {
        this.contentText = contentText;
    }

    public String getContentDelta() {
        return contentDelta;
    }

    public void setContentDelta(String contentDelta) {
        this.contentDelta = contentDelta;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "TodoRequestDTO{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", contentText='" + contentText + '\'' +
                ", contentDelta='" + contentDelta + '\'' +
                ", dueDate=" + dueDate +
                ", priority='" + priority + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
