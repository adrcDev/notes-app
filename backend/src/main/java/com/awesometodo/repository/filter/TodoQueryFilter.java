package com.awesometodo.repository.filter;

public class TodoQueryFilter {
    private String titleSearch;
    private String descriptionSearch;
    private String contentSearch;
    private String priority;
    private String status;
    private String dueDateFrom;
    private String dueDateTo;

    public TodoQueryFilter() {}

    public TodoQueryFilter(String titleSearch, String descriptionSearch, String contentSearch, String priority, String status, String dueDateFrom, String dueDateTo) {
        this.titleSearch = titleSearch;
        this.descriptionSearch = descriptionSearch;
        this.contentSearch = contentSearch;
        this.priority = priority;
        this.status = status;
        this.dueDateFrom = dueDateFrom;
        this.dueDateTo = dueDateTo;
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

    @Override
    public String toString() {
        return "TodoQueryFilter{" +
                "titleSearch='" + titleSearch + '\'' +
                ", descriptionSearch='" + descriptionSearch + '\'' +
                ", contentSearch='" + contentSearch + '\'' +
                ", priority='" + priority + '\'' +
                ", status='" + status + '\'' +
                ", dueDateFrom='" + dueDateFrom + '\'' +
                ", dueDateTo='" + dueDateTo + '\'' +
                '}';
    }
}
