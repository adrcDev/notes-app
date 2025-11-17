package com.awesometodo.validation.validator;

import com.awesometodo.validation.constraint.ValidTodoSortOrder;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidTodoSortOrderValidator implements ConstraintValidator<ValidTodoSortOrder,String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if(value==null)
            return true;

        if(value.equals("ascending") || value.equals("descending"))
            return true;
        else
            return false;
    }
}
