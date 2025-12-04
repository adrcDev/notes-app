package com.awesometodo.repository;

import com.awesometodo.entity.Todo;
import com.awesometodo.repository.criteria.TodoQueryCriteria;
import com.awesometodo.repository.filter.TodoQueryFilter;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Repository
public class TodoRepository {
    EntityManager em;

    public TodoRepository(EntityManager em) {
        this.em=em;
    }

    public List<Todo> findByUserIdAndQueryCriteria(int userId, TodoQueryCriteria todoQueryCriteria) {
        String queryBeginning="SELECT * FROM todos WHERE user_id=:userId";
        HashMap<String,Object> parameterMap=new HashMap<>();
        parameterMap.put("userId",userId);
        String columnNameToSortBy=getColumnNameToSortBy(todoQueryCriteria);
        String orderByClause;
        String sortOrder=getSortOrder(todoQueryCriteria);
        if(columnNameToSortBy.equals("priority")) {
            orderByClause="ORDER BY CASE priority WHEN 'low' THEN 1 WHEN 'medium' THEN 2 WHEN 'high' THEN 3 END"+" "+sortOrder;
        }
        else {
            orderByClause="ORDER BY "+columnNameToSortBy+" "+sortOrder;
        }
        String limitAndOffsetClauses="LIMIT "+todoQueryCriteria.getLimit()+" "+"OFFSET "+todoQueryCriteria.getOffset();

        StringBuilder queryStringBuilder=new StringBuilder(queryBeginning);
        if(todoQueryCriteria.getTitleSearch()!=null) {
            queryStringBuilder.append(" AND title_tsvector @@ websearch_to_tsquery('english',:titleSearch)");
            parameterMap.put("titleSearch",todoQueryCriteria.getTitleSearch());
        }
        if(todoQueryCriteria.getDescriptionSearch()!=null) {
            queryStringBuilder.append(" AND description_tsvector @@ websearch_to_tsquery('english',:descriptionSearch)");
            parameterMap.put("descriptionSearch",todoQueryCriteria.getDescriptionSearch());
        }
        if(todoQueryCriteria.getContentSearch()!=null) {
            queryStringBuilder.append(" AND content_text_tsvector @@ websearch_to_tsquery('english',:contentSearch)");
            parameterMap.put("contentSearch",todoQueryCriteria.getContentSearch());
        }
        if(todoQueryCriteria.getPriority()!=null) {
            queryStringBuilder.append(" AND priority=:priority");
            parameterMap.put("priority",todoQueryCriteria.getPriority());
        }
        if(todoQueryCriteria.getStatus()!=null) {
            queryStringBuilder.append(" AND status=:status");
            parameterMap.put("status",todoQueryCriteria.getStatus());
        }

        if(todoQueryCriteria.getDueDateFrom()!=null && todoQueryCriteria.getDueDateTo()!=null) {
            queryStringBuilder.append(" AND due_date>=:dueDateFrom AND due_date<=:dueDateTo");
            parameterMap.put("dueDateFrom", LocalDate.parse(todoQueryCriteria.getDueDateFrom()));
            parameterMap.put("dueDateTo",LocalDate.parse(todoQueryCriteria.getDueDateTo()));
        }
        else if(todoQueryCriteria.getDueDateFrom()!=null) {
            queryStringBuilder.append(" AND due_date>=:dueDateFrom");
            parameterMap.put("dueDateFrom", LocalDate.parse(todoQueryCriteria.getDueDateFrom()));
        }
        else if(todoQueryCriteria.getDueDateTo()!=null) {
            queryStringBuilder.append(" AND due_date<=:dueDateTo");
            parameterMap.put("dueDateTo",LocalDate.parse(todoQueryCriteria.getDueDateTo()));
        }

        queryStringBuilder.append(" ").append(orderByClause).append(" ").append(limitAndOffsetClauses);
        String constructedQuery=queryStringBuilder.toString();
        Query queryObj=em.createNativeQuery(constructedQuery, Todo.class);
        for(Map.Entry<String,Object> parameterKeyValuePair: parameterMap.entrySet()) {
            queryObj.setParameter(parameterKeyValuePair.getKey(),parameterKeyValuePair.getValue());
        }
        List<Todo> todosMatchingQueryCriteria=queryObj.getResultList();
        return todosMatchingQueryCriteria;
    }

    private String getColumnNameToSortBy(TodoQueryCriteria todoQueryCriteria) {
        String columnNameToSortBy="";
        if(todoQueryCriteria.getSortBy().equals("due date"))
            columnNameToSortBy= "due_date";
        else if(todoQueryCriteria.getSortBy().equals("priority"))
            columnNameToSortBy="priority";
        else if(todoQueryCriteria.getSortBy().equals("creation date and time"))
            columnNameToSortBy="created_at";
        else if(todoQueryCriteria.getSortBy().equals("last updation date and time"))
            columnNameToSortBy="updated_at";
        return  columnNameToSortBy;
    }

    private String getSortOrder(TodoQueryCriteria todoQueryCriteria) {
        if(todoQueryCriteria.getSortOrder().equals("ascending"))
            return "ASC";
        else
            return "DESC";
    }

    public int findCountByUserIdAndQueryFilter(int userId, TodoQueryFilter todoQueryFilter) {
        String queryBeginning="SELECT COUNT(*) FROM todos WHERE user_id=:userId";
        HashMap<String,Object> parameterMap=new HashMap<>();
        parameterMap.put("userId",userId);

        StringBuilder queryStringBuilder=new StringBuilder(queryBeginning);
        if(todoQueryFilter.getTitleSearch()!=null) {
            queryStringBuilder.append(" AND title_tsvector @@ websearch_to_tsquery('english',:titleSearch)");
            parameterMap.put("titleSearch",todoQueryFilter.getTitleSearch());
        }
        if(todoQueryFilter.getDescriptionSearch()!=null) {
            queryStringBuilder.append(" AND description_tsvector @@ websearch_to_tsquery('english',:descriptionSearch)");
            parameterMap.put("descriptionSearch",todoQueryFilter.getDescriptionSearch());
        }
        if(todoQueryFilter.getContentSearch()!=null) {
            queryStringBuilder.append(" AND content_text_tsvector @@ websearch_to_tsquery('english',:contentSearch)");
            parameterMap.put("contentSearch",todoQueryFilter.getContentSearch());
        }
        if(todoQueryFilter.getPriority()!=null) {
            queryStringBuilder.append(" AND priority=:priority");
            parameterMap.put("priority",todoQueryFilter.getPriority());
        }
        if(todoQueryFilter.getStatus()!=null) {
            queryStringBuilder.append(" AND status=:status");
            parameterMap.put("status",todoQueryFilter.getStatus());
        }

        if(todoQueryFilter.getDueDateFrom()!=null && todoQueryFilter.getDueDateTo()!=null) {
            queryStringBuilder.append(" AND due_date>=:dueDateFrom AND due_date<=:dueDateTo");
            parameterMap.put("dueDateFrom", LocalDate.parse(todoQueryFilter.getDueDateFrom()));
            parameterMap.put("dueDateTo",LocalDate.parse(todoQueryFilter.getDueDateTo()));
        }
        else if(todoQueryFilter.getDueDateFrom()!=null) {
            queryStringBuilder.append(" AND due_date>=:dueDateFrom");
            parameterMap.put("dueDateFrom", LocalDate.parse(todoQueryFilter.getDueDateFrom()));
        }
        else if(todoQueryFilter.getDueDateTo()!=null) {
            queryStringBuilder.append(" AND due_date<=:dueDateTo");
            parameterMap.put("dueDateTo",LocalDate.parse(todoQueryFilter.getDueDateTo()));
        }

        String constructedQuery=queryStringBuilder.toString();
        Query queryObj=em.createNativeQuery(constructedQuery, Integer.class);
        for(Map.Entry<String,Object> parameterKeyValuePair: parameterMap.entrySet()) {
            queryObj.setParameter(parameterKeyValuePair.getKey(),parameterKeyValuePair.getValue());
        }
        int matchingTodosCount=(Integer)queryObj.getSingleResult();
        return matchingTodosCount;
    }

    public boolean isExistsByIdAndUserId(int todoId, int userId) {
        boolean result=(boolean)em.createNativeQuery("SELECT COUNT(*)=1 FROM todos WHERE id=:todoId AND user_id=:userId",Boolean.class).setParameter("todoId",todoId).setParameter("userId",userId).getSingleResult();
        return result;
    }

    public void deleteByIdAndUserId(int todoId,int userId) {
        em.createNativeQuery("DELETE FROM todos WHERE id=:todoId AND user_id=:userId").setParameter("todoId",todoId).setParameter("userId",userId).executeUpdate();
    }


}
