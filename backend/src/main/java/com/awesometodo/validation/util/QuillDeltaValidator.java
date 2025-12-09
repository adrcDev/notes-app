package com.awesometodo.validation.util;

import com.awesometodo.SpringApplicationContext;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;

public class QuillDeltaValidator {
    private QuillDeltaValidator() {}

    public static boolean isValid(String value) {
        ObjectMapper objectMapper=SpringApplicationContext.getBean(ObjectMapper.class);
        boolean isValidJson;
        JsonNode jsonNode=null;
        try {
            jsonNode=objectMapper.readTree(value);
            isValidJson=true;
        } catch(JacksonException e) {
            isValidJson=false;
        }

        if(!isValidJson)
            return false;

        boolean isJsonNodeNotAnObject=jsonNode.getNodeType()!=JsonNodeType.OBJECT;
        if(isJsonNodeNotAnObject) {
            return false;
        }

        int noOfFieldsInObject=jsonNode.size();
        if(noOfFieldsInObject!=1) {
            return false;
        }

        String fieldName="";
        Iterator<String> objectFieldNamesIterator=jsonNode.fieldNames();
        if(objectFieldNamesIterator.hasNext()) {
            fieldName=objectFieldNamesIterator.next();
        }

        boolean isFieldNameNotOps=!fieldName.equals("ops");
        if(isFieldNameNotOps)
            return false;

        Iterator<JsonNode> fieldValuesOfObject=jsonNode.elements();
        JsonNode fieldValueOfOpsField=null;
        while(fieldValuesOfObject.hasNext()) {
            fieldValueOfOpsField=fieldValuesOfObject.next();
        }

        boolean isFieldValueOfOpsFieldNotAnArray=fieldValueOfOpsField.getNodeType()!=JsonNodeType.ARRAY;
        if(isFieldValueOfOpsFieldNotAnArray) {
            return false;
        }

        return true;
    }
}
