package com.awesometodo.repository.criteria;

import com.awesometodo.validation.constraint.*;
import jakarta.validation.constraints.Min;

public class TodoQueryCriteria {
    private String titleSearch;
    private String descriptionSearch;
    private String contentSearch;
    private String priority;
    private String status;
    private String dueDateFrom;
    private String dueDateTo;
    private String sortBy;
    private String sortOrder;
    private int limit;
    private int offset;

    public TodoQueryCriteria() {}

    public TodoQueryCriteria(String titleSearch, String descriptionSearch, String contentSearch, String priority, String status, String dueDateFrom, String dueDateTo, String sortBy, String sortOrder, int limit, int offset) {
        this.titleSearch = titleSearch;
        this.descriptionSearch = descriptionSearch;
        this.contentSearch = contentSearch;
        this.priority = priority;
        this.status = status;
        this.dueDateFrom = dueDateFrom;
        this.dueDateTo = dueDateTo;
        this.sortBy = sortBy;
        this.sortOrder = sortOrder;
        this.limit = limit;
        this.offset = offset;
    }

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
        return "TodoQueryCriteria{" +
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
