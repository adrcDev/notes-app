package com.awesometodo.dto;

import com.awesometodo.validation.constraint.*;
import jakarta.validation.constraints.Min;

@ValidTodoDueDateRange
public class TodoQueryParamsDTO {
    @NotEmptyString(message = "titleSearch query parameter value cannot be empty string and needs to include at least one non whitespace character")
    private String titleSearch;

    @NotEmptyString(message = "descriptionSearch query parameter value cannot be empty string and needs to include at least one non whitespace character")
    private String descriptionSearch;

    @NotEmptyString(message = "contentSearch query parameter value cannot be empty string and needs to include at least one non whitespace character")
    private String contentSearch;

    @NotEmptyString(message = "priority query parameter value cannot be empty string and needs to be one of the valid todo priority values")
    @ValidTodoPriority(message="Received priority query parameter value is not a valid todo priority value")
    @LowerCaseString(message="Received priority query parameter value is not fully lowercase")
    private String priority;

    @NotEmptyString(message = "status query parameter value cannot be empty string and needs to be one of the valid todo status values")
    @ValidTodoStatus(message="Received status query parameter value is not a valid todo status value")
    @LowerCaseString(message="Received status query parameter value is not fully lowercase")
    private String status;

    @NotEmptyString(message="dueDateFrom query parameter value cannot be empty string and needs to be a valid date string in YYYY-MM-DD format")
    @DateStringYYYYMMDD(message="Received dueDateFrom query parameter value is not in YYYY-MM-DD format")
    private String dueDateFrom;

    @NotEmptyString(message="dueDateTo query parameter value cannot be empty string and needs to be a valid date string in YYYY-MM-DD format")
    @DateStringYYYYMMDD(message="Received dueDateTo query parameter value is not in YYYY-MM-DD format")
    private String dueDateTo;

    @NotEmptyString(message="sortBy query parameter value cannot be empty string and needs to be one of the valid sortBy values")
    @ValidTodoSortBy(message="Received sortBy query parameter value is not a valid todo sort by value")
    @LowerCaseString(message="Received sortBy query parameter value is not fully lowercase")
    private String sortBy="due date";

    @NotEmptyString(message="sortOrder query parameter value cannot be empty string and needs to be one of the valid sort order values")
    @ValidTodoSortOrder(message = "Received sortOrder query parameter value is not a valid todo sort order value")
    private String sortOrder="ascending";

    @Min(value=0,message = "limit query parameter value cannot be a negative number")
    private int limit=10;

    @Min(value=0,message = "offset query parameter value cannot be a negative number")
    private int offset=0;

    public String getTitleSearch() {
        return titleSearch;
    }

    public void setTitleSearch(String titleSearch) {
        this.titleSearch = titleSearch;
    }

    public String getDescriptionSearch() {
        return descriptionSearch;
    }

    public void setDescriptionSearch(String descriptionSearch) {
        this.descriptionSearch = descriptionSearch;
    }

    public String getContentSearch() {
        return contentSearch;
    }

    public void setContentSearch(String contentSearch) {
        this.contentSearch = contentSearch;
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

    public String getDueDateFrom() {
        return dueDateFrom;
    }

    public void setDueDateFrom(String dueDateFrom) {
        this.dueDateFrom = dueDateFrom;
    }

    public String getDueDateTo() {
        return dueDateTo;
    }

    public void setDueDateTo(String dueDateTo) {
        this.dueDateTo = dueDateTo;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public String getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    @Override
    public String toString() {
        return "TodoFilterAndSortQueryParamsDTO{" +
                "titleSearch='" + titleSearch + '\'' +
                ", descriptionSearch='" + descriptionSearch + '\'' +
                ", contentSearch='" + contentSearch + '\'' +
                ", priority='" + priority + '\'' +
                ", status='" + status + '\'' +
                ", dueDateFrom='" + dueDateFrom + '\'' +
                ", dueDateTo='" + dueDateTo + '\'' +
                ", sortBy='" + sortBy + '\'' +
                ", sortOrder='" + sortOrder + '\'' +
                ", limit=" + limit +
                ", offset=" + offset +
                '}';
    }
}
