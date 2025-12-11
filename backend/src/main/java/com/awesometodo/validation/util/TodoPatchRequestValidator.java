package com.awesometodo.validation.util;

import com.awesometodo.entity.Todo;
import com.awesometodo.exception.PatchTodoRequestValidationException;
import com.awesometodo.util.EnumUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class TodoPatchRequestValidator {
    private TodoPatchRequestValidator() {}

    public static  Map<String,String> validateAndReturnMap(JsonNode requestBodyJsonNode) {
        Map<String,String> todoUpdateFieldsMap=new HashMap<>();
        boolean isJsonNodeNotAJsonObject=requestBodyJsonNode.getNodeType()!= JsonNodeType.OBJECT;
        if(isJsonNodeNotAJsonObject) {
            throw new PatchTodoRequestValidationException();
        }

        int noOfFieldsInJsonObject=requestBodyJsonNode.size();
        boolean isJsonObjectDoesntContainAnyFields=noOfFieldsInJsonObject==0;
        if(isJsonObjectDoesntContainAnyFields) {
            throw new PatchTodoRequestValidationException();
        }

        JsonNode titleFieldValueJsonNode=requestBodyJsonNode.get("title");
        boolean isTitleFieldPresentInJsonObject=titleFieldValueJsonNode!=null;
        if(isTitleFieldPresentInJsonObject) {
            boolean isTitleFieldValueNotTextNorNull=
                    !(titleFieldValueJsonNode.isTextual() || titleFieldValueJsonNode.isNull());
            if(isTitleFieldValueNotTextNorNull) {
                throw new PatchTodoRequestValidationException();
            }

            if(titleFieldValueJsonNode.isTextual()) {
                String titleFieldValue=titleFieldValueJsonNode.asText();
                todoUpdateFieldsMap.put("title",titleFieldValue);
            }
            else {
                todoUpdateFieldsMap.put("title",null);
            }
        }

        JsonNode descriptionFieldValueJsonNode=requestBodyJsonNode.get("description");
        boolean isDescriptionFieldPresentInJsonObject=descriptionFieldValueJsonNode!=null;
        if(isDescriptionFieldPresentInJsonObject) {
            boolean isDescriptionFieldValueNotTextNorNull=
                    !(descriptionFieldValueJsonNode.isTextual() || descriptionFieldValueJsonNode.isNull());
            if(isDescriptionFieldValueNotTextNorNull) {
                throw new PatchTodoRequestValidationException();
            }

            if(descriptionFieldValueJsonNode.isTextual()) {
                String descriptionFieldValue=descriptionFieldValueJsonNode.asText();
                todoUpdateFieldsMap.put("description",descriptionFieldValue);
            }
            else {
                todoUpdateFieldsMap.put("description",null);
            }
        }

        JsonNode contentTextFieldValueJsonNode=requestBodyJsonNode.get("contentText");
        JsonNode contentDeltaFieldValueJsonNode=requestBodyJsonNode.get("contentDelta");
        boolean isContentTextFieldPresentInJsonObject=contentTextFieldValueJsonNode!=null;
        boolean isContentDeltaFieldPresentInJsonObject=contentDeltaFieldValueJsonNode!=null;
        boolean isContentTextAndDeltaPairNotPresentInJsonObject=
                (isContentTextFieldPresentInJsonObject && !isContentDeltaFieldPresentInJsonObject) ||
                        (!isContentTextFieldPresentInJsonObject && isContentDeltaFieldPresentInJsonObject);
        if(isContentTextAndDeltaPairNotPresentInJsonObject) {
            throw new PatchTodoRequestValidationException();
        }

        if(isContentTextFieldPresentInJsonObject && isContentDeltaFieldPresentInJsonObject) {
            boolean isContentTextFieldValueNotTextNorNull=
                    !(contentTextFieldValueJsonNode.isTextual() || contentTextFieldValueJsonNode.isNull());
            if(isContentTextFieldValueNotTextNorNull) {
                throw new PatchTodoRequestValidationException();
            }
            boolean isContentDeltaFieldValueNotTextNorNull=
                    !(contentDeltaFieldValueJsonNode.isTextual() || contentDeltaFieldValueJsonNode.isNull());
            if(isContentDeltaFieldValueNotTextNorNull) {
                throw new PatchTodoRequestValidationException();
            }

            boolean isOneNonNullAndAnotherNull=
                    (contentTextFieldValueJsonNode.isTextual() && contentDeltaFieldValueJsonNode.isNull()) || (contentTextFieldValueJsonNode.isNull() && contentDeltaFieldValueJsonNode.isTextual());
            if(isOneNonNullAndAnotherNull) {
                throw new PatchTodoRequestValidationException();
            }

            if(contentTextFieldValueJsonNode.isTextual()) {
                String contentTextFieldValue=contentTextFieldValueJsonNode.asText();
                todoUpdateFieldsMap.put("contentText",contentTextFieldValue);
            }
            else {
                todoUpdateFieldsMap.put("contentText",null);
            }

            if(contentDeltaFieldValueJsonNode.isTextual()) {
                String contentDeltaFieldValue=contentDeltaFieldValueJsonNode.asText();
                if(contentDeltaFieldValue.trim().equals("")) {
                    throw new PatchTodoRequestValidationException();
                }
                if(!QuillDeltaValidator.isValid(contentDeltaFieldValue)) {
                    throw new PatchTodoRequestValidationException();
                }
                todoUpdateFieldsMap.put("contentDelta",contentDeltaFieldValue);
            }
            else {
                todoUpdateFieldsMap.put("contentDelta",null);
            }
        }


        JsonNode dueDateFieldValueJsonNode=requestBodyJsonNode.get("dueDate");
        boolean isDueDateFieldPresentInJsonObject=dueDateFieldValueJsonNode!=null;
        if(isDueDateFieldPresentInJsonObject) {
            boolean isDueDateFieldValueNotTextNorNull=
                    !(dueDateFieldValueJsonNode.isTextual() || dueDateFieldValueJsonNode.isNull());
            if(isDueDateFieldValueNotTextNorNull) {
                throw new PatchTodoRequestValidationException();
            }

            if(dueDateFieldValueJsonNode.isTextual()) {
                String dueDateFieldValue=dueDateFieldValueJsonNode.asText();
                try {
                    LocalDate.parse(dueDateFieldValue);
                } catch(DateTimeParseException e) {
                    throw new PatchTodoRequestValidationException();
                }
                todoUpdateFieldsMap.put("dueDate",dueDateFieldValue);
            }
            else {
                todoUpdateFieldsMap.put("dueDate",null);
            }
        }

        JsonNode priorityFieldValueJsonNode=requestBodyJsonNode.get("priority");
        boolean isPriorityFieldPresentInJsonObject=priorityFieldValueJsonNode!=null;
        if(isPriorityFieldPresentInJsonObject) {
            boolean isPriorityFieldValueNotTextNorNull=
                    !(priorityFieldValueJsonNode.isTextual() || priorityFieldValueJsonNode.isNull());
            if(isPriorityFieldValueNotTextNorNull) {
                throw new PatchTodoRequestValidationException();
            }

            if(priorityFieldValueJsonNode.isTextual()) {
                String priorityFieldValue=priorityFieldValueJsonNode.asText();
                Optional<Todo.Priority> optionalPriorityEnum= EnumUtil.convertStringToSpecifiedEnumClassConstant(priorityFieldValue, Todo.Priority.class);
                boolean isPriorityFieldValueNotValid=optionalPriorityEnum.isEmpty();
                if(isPriorityFieldValueNotValid) {
                    throw new PatchTodoRequestValidationException();
                }
                todoUpdateFieldsMap.put("priority",priorityFieldValue);
            }
            else {
                todoUpdateFieldsMap.put("priority",null);
            }
        }

        JsonNode statusFieldValueJsonNode=requestBodyJsonNode.get("status");
        boolean isStatusFieldPresentInJsonObject=statusFieldValueJsonNode!=null;
        if(isStatusFieldPresentInJsonObject) {
            boolean isStatusFieldValueNotTextNorNull=
                    !(statusFieldValueJsonNode.isTextual() || statusFieldValueJsonNode.isNull());
            if(isStatusFieldValueNotTextNorNull) {
                throw new PatchTodoRequestValidationException();
            }

            if(statusFieldValueJsonNode.isTextual()) {
                String statusFieldValue=statusFieldValueJsonNode.asText();
                Optional<Todo.Status> optionalStatusEnum= EnumUtil.convertStringToSpecifiedEnumClassConstant(statusFieldValue, Todo.Status.class);
                boolean isStatusFieldValueNotValid=optionalStatusEnum.isEmpty();
                if(isStatusFieldValueNotValid) {
                    throw new PatchTodoRequestValidationException();
                }
                todoUpdateFieldsMap.put("status",statusFieldValue);
            }
            else {
                todoUpdateFieldsMap.put("status",null);
            }
        }

        return todoUpdateFieldsMap;
    }
}
