package com.awesometodo.validation.validator;

import com.awesometodo.enums.TodoSortBy;
import com.awesometodo.util.EnumUtil;
import com.awesometodo.validation.constraint.ValidTodoSortBy;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Optional;

public class ValidTodoSortByValidator implements ConstraintValidator<ValidTodoSortBy,String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if(value==null)
            return true;

        Optional<com.awesometodo.enums.TodoSortBy> optional=EnumUtil.convertStringToSpecifiedEnumClassConstant(value, TodoSortBy.class);
        boolean isReceivedValueAValidTodoSortByValue=!optional.isEmpty();
        return isReceivedValueAValidTodoSortByValue;
    }
}
