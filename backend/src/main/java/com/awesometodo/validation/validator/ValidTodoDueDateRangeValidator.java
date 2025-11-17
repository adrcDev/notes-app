package com.awesometodo.validation.validator;

import com.awesometodo.dto.TodoQueryParamsDTO;
import com.awesometodo.validation.constraint.ValidTodoDueDateRange;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class ValidTodoDueDateRangeValidator implements ConstraintValidator<ValidTodoDueDateRange, TodoQueryParamsDTO> {

    @Override
    public boolean isValid(TodoQueryParamsDTO obj, ConstraintValidatorContext context) {
        if(obj==null)
            return true;

        String possiblyInvalidDueDateFrom=obj.getDueDateFrom();
        String possiblyInvalidDueDateTo=obj.getDueDateTo();
        if(possiblyInvalidDueDateFrom==null || possiblyInvalidDueDateTo==null)
            return true;

        LocalDate dueDateFrom;
        LocalDate dueDateTo;
        try {
            dueDateFrom=LocalDate.parse(possiblyInvalidDueDateFrom);
            dueDateTo=LocalDate.parse(possiblyInvalidDueDateTo);
        } catch(DateTimeParseException e) {
            return true;
        }

        if(dueDateFrom.equals(dueDateTo)) {
            return true;
        }

        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate("Received dueDateFrom:"+dueDateFrom+" and dueDateTo:"+dueDateTo+" query parameter values do not form a valid todo due date range").addConstraintViolation();
        return dueDateFrom.isBefore(dueDateTo);
    }
}
