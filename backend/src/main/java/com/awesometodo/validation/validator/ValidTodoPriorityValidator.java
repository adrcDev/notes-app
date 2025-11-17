package com.awesometodo.validation.validator;

import com.awesometodo.entity.Todo;
import com.awesometodo.util.EnumUtil;
import com.awesometodo.validation.constraint.ValidTodoPriority;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Locale;
import java.util.Optional;

public class ValidTodoPriorityValidator implements ConstraintValidator<ValidTodoPriority,String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if(value==null)
            return true;

       Optional<Todo.Priority> optional=EnumUtil.convertStringToSpecifiedEnumClassConstant(value,Todo.Priority.class);
       boolean isReceivedValueAValidTodoPriority=!optional.isEmpty();
        return isReceivedValueAValidTodoPriority;
    }
}
