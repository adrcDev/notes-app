package com.awesometodo.entity;

import com.awesometodo.entity.converter.GenderEnumToStringConverter;
import com.awesometodo.entity.converter.TodoPriorityEnumToStringConverter;
import com.awesometodo.entity.converter.TodoStatusEnumToStringConverter;
import jakarta.persistence.*;
import org.hibernate.annotations.ColumnTransformer;
import org.hibernate.annotations.JdbcType;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.dialect.PostgreSQLCastingJsonJdbcType;
import org.hibernate.type.SqlTypes;

import java.sql.Types;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Table(name = "todos")
public class Todo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name="title",nullable = false)
    private String title;

    @Column(name="description",nullable = true)
    private String description;

    @Column(name="content_text",nullable = true)
    private String contentText;


    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name="content_delta",nullable = true)
    private String contentDelta;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name = "user_id",referencedColumnName = "id",nullable = false)
    private User user;

    @Column(name="due_date",nullable = true)
    private LocalDate dueDate;

    public enum Priority {
        LOW,MEDIUM,HIGH;
    }

    @Column(name="priority",nullable = false)
    @Convert(converter = TodoPriorityEnumToStringConverter.class)
    private Priority priority;

    public enum Status {
        NOT_COMPLETED,COMPLETED;
    }

    @Column(name="status",nullable = false)
    @Convert(converter = TodoStatusEnumToStringConverter.class)
    private Status status;

    @Column(name="created_at",nullable = false,insertable = false,updatable = false)
    private OffsetDateTime createdAt;

    @Column(name="updated_at",nullable = false,insertable = false)
    private OffsetDateTime updatedAt;

    public Todo() {}

    public Todo(String title, String description, String contentText, String contentDelta, User user, LocalDate dueDate, Priority priority, Status status) {
        this.title = title;
        this.description = description;
        this.contentText = contentText;
        this.contentDelta = contentDelta;
        this.user = user;
        this.dueDate = dueDate;
        this.priority = priority;
        this.status = status;
    }

    public int getId() {
        return id;
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

    public User getUser() {
        return user;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public Priority getPriority() {
        return priority;
    }

    public Status getStatus() {
        return status;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setContentText(String contentText) {
        this.contentText = contentText;
    }

    public void setContentDelta(String contentDelta) {
        this.contentDelta = contentDelta;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "Todo{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", contentText='" + contentText + '\'' +
                ", contentDelta='" + contentDelta + '\'' +
                ", user=" + user +
                ", dueDate=" + dueDate +
                ", priority=" + priority +
                ", status=" + status +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
