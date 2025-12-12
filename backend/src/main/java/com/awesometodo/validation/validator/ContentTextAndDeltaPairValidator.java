package com.awesometodo.validation.validator;

import com.awesometodo.dto.TodoPutRequestDTO;
import com.awesometodo.validation.constraint.ContentTextAndDeltaPair;
import com.awesometodo.validation.util.QuillDeltaValidator;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ContentTextAndDeltaPairValidator implements ConstraintValidator<ContentTextAndDeltaPair, TodoPutRequestDTO> {

    @Override
    public boolean isValid(TodoPutRequestDTO obj, ConstraintValidatorContext context) {
        if(obj==null)
            return true;

        String contentText=obj.getContentText();
        String contentDelta=obj.getContentDelta();
        if(contentText==null && contentDelta==null) {
            return true;
        }

        if(contentDelta!=null) {
            boolean isContentDeltaEmptyString = contentDelta.trim().equals("");
            if (isContentDeltaEmptyString) {
                return true;
            }

            boolean isContentDeltaNotValid = !QuillDeltaValidator.isValid(contentDelta);
            if (isContentDeltaNotValid) {
                return true;
            }
        }

        if(contentText!=null && contentDelta==null) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("contentText json key contains a string value while contentDelta json key contains null. This is not allowed. Json keys contentText and contentDelta can both together contain null or both together can contain string values, only these two conditions are allowed").addConstraintViolation();
            return false;
        }

        if(contentText==null && contentDelta!=null) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("contentText json key contains null while contentDelta json key contains a string value. This is not allowed. Json keys contentText and contentDelta can both together contain null or both together can contain string values, only these two conditions are allowed").addConstraintViolation();
            return false;
        }

        return true;
    }
}
