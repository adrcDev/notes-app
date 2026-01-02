package com.awesometodo.validation.util;

import com.awesometodo.entity.Todo;
import com.awesometodo.exception.PatchTodoRequestValidationException;
import com.awesometodo.util.EnumUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class TodoPatchRequestValidator {
    private static final Logger logger= LoggerFactory.getLogger(TodoPatchRequestValidator.class);

    private TodoPatchRequestValidator() {}

    public static  Map<String,String> validateAndReturnMap(JsonNode requestBodyJsonNode) {
        logger.debug("Validation of http request message's body's json started");
        Map<String,String> todoUpdateFieldsMap=new HashMap<>();
        boolean isJsonNodeNotAJsonObject=requestBodyJsonNode.getNodeType()!= JsonNodeType.OBJECT;
        if(isJsonNodeNotAJsonObject) {
            logger.warn("The http request message's body contains valid json but is not a json object. Aborting the partial todo update process.");
            throw new PatchTodoRequestValidationException();
        }
        int noOfFieldsInJsonObject=requestBodyJsonNode.size();
        logger.debug("The received json object contains {} fields",noOfFieldsInJsonObject);
        boolean isJsonObjectDoesntContainAnyFields=noOfFieldsInJsonObject==0;
        if(isJsonObjectDoesntContainAnyFields) {
            logger.warn("The received json object is empty i.e.., it doesn't contain any fields. Atleast one of the fields supported by this endpoint must be specified (contentText and contentDelta fields are an exception as they need to be specified together always). Aborting the partial todo update process.");
            throw new PatchTodoRequestValidationException();
        }
        validateTitleFieldIfExists(requestBodyJsonNode,todoUpdateFieldsMap);
        validateDescriptionFieldIfExists(requestBodyJsonNode,todoUpdateFieldsMap);
        validateContentTextAndContentDeltaPairIfExists(requestBodyJsonNode,todoUpdateFieldsMap);
        validateDueDateFieldIfExists(requestBodyJsonNode,todoUpdateFieldsMap);
        validatePriorityFieldIfExists(requestBodyJsonNode,todoUpdateFieldsMap);
        validateStatusFieldIfExists(requestBodyJsonNode,todoUpdateFieldsMap);

        boolean isJsonObjectDoesntContainAtLeastOneSupportedField=todoUpdateFieldsMap.size()==0;
        if(isJsonObjectDoesntContainAtLeastOneSupportedField) {
            logger.warn("The received json object has one or more fields but doesn't contain even one of the fields supported by this endpoint. Aborting partial todo update process.");
            throw new PatchTodoRequestValidationException();
        }
        logger.debug("Validation of http request message's body's json was successful");
        return todoUpdateFieldsMap;
    }

    private static void validateTitleFieldIfExists(JsonNode requestBodyJsonNode, Map<String,String> todoUpdateFieldsMap) {
        JsonNode titleFieldValueJsonNode=requestBodyJsonNode.get("title");
        boolean isTitleFieldPresentInJsonObject=titleFieldValueJsonNode!=null;
        if(isTitleFieldPresentInJsonObject) {
            logger.debug("The received json object contains the title field");
            boolean isTitleFieldValueNotTextNorNull=
                    !(titleFieldValueJsonNode.isTextual() || titleFieldValueJsonNode.isNull());
            if(isTitleFieldValueNotTextNorNull) {
                logger.warn("The title field value is neither a string nor null. Aborting partial todo update process.");
                throw new PatchTodoRequestValidationException();
            }

            if(titleFieldValueJsonNode.isTextual()) {
                logger.debug("The title field value is a string. Adding it to todo update fields map.");
                String titleFieldValue=titleFieldValueJsonNode.asText();
                todoUpdateFieldsMap.put("title",titleFieldValue);
            }
            else {
                logger.debug("The title field value is null. Adding it to todo update fields map.");
                todoUpdateFieldsMap.put("title",null);
            }
        }
    }

    private static void validateDescriptionFieldIfExists(JsonNode requestBodyJsonNode,Map<String,String> todoUpdateFieldsMap) {
        JsonNode descriptionFieldValueJsonNode=requestBodyJsonNode.get("description");
        boolean isDescriptionFieldPresentInJsonObject=descriptionFieldValueJsonNode!=null;
        if(isDescriptionFieldPresentInJsonObject) {
            logger.debug("The received json object contains the description field");
            boolean isDescriptionFieldValueNotTextNorNull=
                    !(descriptionFieldValueJsonNode.isTextual() || descriptionFieldValueJsonNode.isNull());
            if(isDescriptionFieldValueNotTextNorNull) {
                logger.warn("The description field value is neither a string nor null. Aborting partial todo update process.");
                throw new PatchTodoRequestValidationException();
            }

            if(descriptionFieldValueJsonNode.isTextual()) {
                logger.debug("The description field value is a string. Adding it to todo update fields map.");
                String descriptionFieldValue=descriptionFieldValueJsonNode.asText();
                todoUpdateFieldsMap.put("description",descriptionFieldValue);
            }
            else {
                logger.debug("The description field value is null. Adding it to todo update fields map.");
                todoUpdateFieldsMap.put("description",null);
            }
        }
    }

    private static void validateContentTextAndContentDeltaPairIfExists(JsonNode requestBodyJsonNode, Map<String,String> todoUpdateFieldsMap) {
        JsonNode contentTextFieldValueJsonNode=requestBodyJsonNode.get("contentText");
        JsonNode contentDeltaFieldValueJsonNode=requestBodyJsonNode.get("contentDelta");
        boolean isContentTextFieldAbsent=contentTextFieldValueJsonNode==null;
        boolean isContentDeltaFieldAbsent=contentDeltaFieldValueJsonNode==null;
        if(isContentTextFieldAbsent && isContentDeltaFieldAbsent) {
            logger.debug("The received json does not contain contentText and contentDelta fields. Skipping contentText and contentDelta and pair validations");
            return;
        }

        boolean isContentTextFieldPresentInJsonObject=contentTextFieldValueJsonNode!=null;
        boolean isContentDeltaFieldPresentInJsonObject=contentDeltaFieldValueJsonNode!=null;
        boolean isContentTextAndDeltaPairNotPresentInJsonObject=
                (isContentTextFieldPresentInJsonObject && !isContentDeltaFieldPresentInJsonObject) ||
                        (!isContentTextFieldPresentInJsonObject && isContentDeltaFieldPresentInJsonObject);
        if(isContentTextAndDeltaPairNotPresentInJsonObject) {
            logger.warn("The received json contains only one of either the contentText and contentDelta fields. This is not allowed both of them must be specified together. Aborting partial todo update process");
            throw new PatchTodoRequestValidationException();
        }

        logger.debug("The received json contains both the contentText and contentDelta fields");
        boolean isContentTextFieldValueNotTextNorNull=
                !(contentTextFieldValueJsonNode.isTextual() || contentTextFieldValueJsonNode.isNull());
        if(isContentTextFieldValueNotTextNorNull) {
            logger.warn("The contentText field value is neither a string nor null. Aborting partial todo update process.");
            throw new PatchTodoRequestValidationException();
        }
        boolean isContentDeltaFieldValueNotTextNorNull=
                !(contentDeltaFieldValueJsonNode.isTextual() || contentDeltaFieldValueJsonNode.isNull());
        if(isContentDeltaFieldValueNotTextNorNull) {
            logger.warn("The contentDelta field value is neither a string nor null. Aborting partial todo update process.");
            throw new PatchTodoRequestValidationException();
        }

        boolean isOneNonNullAndAnotherNull=
                (contentTextFieldValueJsonNode.isTextual() && contentDeltaFieldValueJsonNode.isNull()) || (contentTextFieldValueJsonNode.isNull() && contentDeltaFieldValueJsonNode.isTextual());
        if(isOneNonNullAndAnotherNull) {
            logger.warn("Among the contentText and contentDelta field values, one of the values is non null and the other is null. This is not allowed. They should both either be null or non null together. Aborting partial todo update process");
            throw new PatchTodoRequestValidationException();
        }

        if(contentTextFieldValueJsonNode.isTextual()) {
            logger.debug("The contentText field value is a string. Adding it to todo update fields map");
            String contentTextFieldValue=contentTextFieldValueJsonNode.asText();
            todoUpdateFieldsMap.put("contentText",contentTextFieldValue);
        }
        else {
            logger.debug("The contentText field value is null. Adding it to todo update fields map");
            todoUpdateFieldsMap.put("contentText",null);
        }

        if(contentDeltaFieldValueJsonNode.isTextual()) {
            logger.debug("The contentDelta field value is a string");
            String contentDeltaFieldValue=contentDeltaFieldValueJsonNode.asText();
            if(contentDeltaFieldValue.trim().equals("")) {
                logger.warn("The contentDelta field value is an empty string. This is not allowed as contentDelta field value is expected to be in the quill editor's internal delta format which is a subset of the json format. Aborting the partial todo update process");
                throw new PatchTodoRequestValidationException();
            }
            if(!QuillDeltaValidator.isValid(contentDeltaFieldValue)) {
                logger.warn("The contentDelta field value is not in the quill editor's internal delta format which is a subset of the json format. Aborting the partial todo update process");
                throw new PatchTodoRequestValidationException();
            }
            logger.debug("The contentDelta field value is in the quill editor's internal delta format which is a subset of the json format. Adding it to the todo update fields map");
            todoUpdateFieldsMap.put("contentDelta",contentDeltaFieldValue);
        }
        else {
            logger.debug("The contentDelta field value is null. Adding it to todo update fields map");
            todoUpdateFieldsMap.put("contentDelta",null);
        }

    }

    private static void validateDueDateFieldIfExists(JsonNode requestBodyJsonNode,Map<String,String> todoUpdateFieldsMap) {
        JsonNode dueDateFieldValueJsonNode=requestBodyJsonNode.get("dueDate");
        boolean isDueDateFieldPresentInJsonObject=dueDateFieldValueJsonNode!=null;
        if(isDueDateFieldPresentInJsonObject) {
            logger.debug("The received json contains the dueDate field");
            boolean isDueDateFieldValueNotTextNorNull=
                    !(dueDateFieldValueJsonNode.isTextual() || dueDateFieldValueJsonNode.isNull());
            if(isDueDateFieldValueNotTextNorNull) {
                logger.warn("The dueDate field value is neither a string nor null. Aborting the partial todo update process");
                throw new PatchTodoRequestValidationException();
            }

            if(dueDateFieldValueJsonNode.isTextual()) {
                logger.debug("The dueDate field value is a string");
                String dueDateFieldValue=dueDateFieldValueJsonNode.asText();
                try {
                    logger.debug("Checking if the dueDate field value string is a valid date in YYYY-MM-DD format");
                    LocalDate.parse(dueDateFieldValue);
                } catch(DateTimeParseException e) {
                    logger.warn("The dueDate field value string is not a valid date in YYYY-MM-DD format. Aborting the partial todo update process");
                    throw new PatchTodoRequestValidationException();
                }
                logger.debug("The dueDate field value string is a valid date in YYYY-MM-DD format. Adding it to todo update fields map");
                todoUpdateFieldsMap.put("dueDate",dueDateFieldValue);
            }
            else {
                logger.debug("The dueDate field value is null. Adding it to todo update fields map");
                todoUpdateFieldsMap.put("dueDate",null);
            }
        }
    }

    private static void validatePriorityFieldIfExists(JsonNode requestBodyJsonNode,Map<String,String> todoUpdateFieldsMap) {
        JsonNode priorityFieldValueJsonNode=requestBodyJsonNode.get("priority");
        boolean isPriorityFieldPresentInJsonObject=priorityFieldValueJsonNode!=null;
        if(isPriorityFieldPresentInJsonObject) {
            logger.debug("The received json object contains the priority field");
            boolean isPriorityFieldValueNotTextNorNull=
                    !(priorityFieldValueJsonNode.isTextual() || priorityFieldValueJsonNode.isNull());
            if(isPriorityFieldValueNotTextNorNull) {
                logger.warn("The priority field value is neither a string nor null. Aborting the partial todo update process");
                throw new PatchTodoRequestValidationException();
            }

            if(priorityFieldValueJsonNode.isTextual()) {
                logger.debug("The priority field value is a string");
                String priorityFieldValue=priorityFieldValueJsonNode.asText();
                Optional<Todo.Priority> optionalPriorityEnum= EnumUtil.convertStringToSpecifiedEnumClassConstant(priorityFieldValue, Todo.Priority.class);
                boolean isPriorityFieldValueNotValid=optionalPriorityEnum.isEmpty();
                if(isPriorityFieldValueNotValid) {
                    logger.warn("The priority field value string is not one of the valid todo priority values. Aborting the partial todo update process");
                    throw new PatchTodoRequestValidationException();
                }
                logger.debug("The priority field value string is one of the valid todo priority values. Adding it to todo update fields map");
                todoUpdateFieldsMap.put("priority",priorityFieldValue);
            }
            else {
                logger.debug("The priority field value is null. Adding it to todo update fields map");
                todoUpdateFieldsMap.put("priority",null);
            }
        }
    }

    private static void validateStatusFieldIfExists(JsonNode requestBodyJsonNode,Map<String,String> todoUpdateFieldsMap) {
        JsonNode statusFieldValueJsonNode=requestBodyJsonNode.get("status");
        boolean isStatusFieldPresentInJsonObject=statusFieldValueJsonNode!=null;
        if(isStatusFieldPresentInJsonObject) {
            logger.debug("The received json contains the status field");
            boolean isStatusFieldValueNotTextNorNull=
                    !(statusFieldValueJsonNode.isTextual() || statusFieldValueJsonNode.isNull());
            if(isStatusFieldValueNotTextNorNull) {
                logger.warn("The status field value is neither a string nor null. Aborting the partial todo update process");
                throw new PatchTodoRequestValidationException();
            }

            if(statusFieldValueJsonNode.isTextual()) {
                logger.debug("The status field value is a string");
                String statusFieldValue=statusFieldValueJsonNode.asText();
                Optional<Todo.Status> optionalStatusEnum= EnumUtil.convertStringToSpecifiedEnumClassConstant(statusFieldValue, Todo.Status.class);
                boolean isStatusFieldValueNotValid=optionalStatusEnum.isEmpty();
                if(isStatusFieldValueNotValid) {
                    logger.warn("The status field value string is not one of the valid todo status values. Aborting the partial todo update process");
                    throw new PatchTodoRequestValidationException();
                }
                logger.debug("The status field value string is one of the valid todo status values. Adding it to todo update fields map");
                todoUpdateFieldsMap.put("status",statusFieldValue);
            }
            else {
                logger.debug("The status field value is null. Adding it to todo update fields map");
                todoUpdateFieldsMap.put("status",null);
            }
        }
    }

}
