package com.awesometodo.validation.validator;

import com.awesometodo.validation.constraint.DateOfBirth;
import com.awesometodo.validation.constraint.NotEmptyString;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NotEmptyStringValidator implements ConstraintValidator<NotEmptyString,String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if(value==null)
            return true;

        if(value.trim().isEmpty())
            return false;
        else
            return true;
    }
}
