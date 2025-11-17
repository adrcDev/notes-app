package com.awesometodo.validation.validator;

import com.awesometodo.entity.Todo;
import com.awesometodo.util.EnumUtil;
import com.awesometodo.validation.constraint.ValidTodoStatus;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Optional;

public class ValidTodoStatusValidator implements ConstraintValidator<ValidTodoStatus,String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if(value==null)
            return true;

        Optional<Todo.Status> optional= EnumUtil.convertStringToSpecifiedEnumClassConstant(value,Todo.Status.class);
        boolean isReceivedValueAValidTodoStatus=!optional.isEmpty();
        return isReceivedValueAValidTodoStatus;
    }
}
