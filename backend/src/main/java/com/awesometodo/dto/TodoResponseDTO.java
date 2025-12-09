package com.awesometodo.dto;

import java.time.LocalDate;
import java.time.OffsetDateTime;

public class TodoResponseDTO {
    private int id;
    private String title;
    private String description;
    private String contentDelta;
    private LocalDate dueDate;
    private String priority;
    private String status;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    //temp
    public TodoResponseDTO() {

    }

    public TodoResponseDTO(int id, String title, String description, String contentDelta, LocalDate dueDate, String priority, String status, OffsetDateTime createdAt, OffsetDateTime updatedAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.contentDelta = contentDelta;
        this.dueDate = dueDate;
        this.priority = priority;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "TodoResponseDTO{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", contentDelta='" + contentDelta + '\'' +
                ", dueDate=" + dueDate +
                ", priority='" + priority + '\'' +
                ", status='" + status + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
