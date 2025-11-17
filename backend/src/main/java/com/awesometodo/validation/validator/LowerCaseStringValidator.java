package com.awesometodo.validation.validator;

import com.awesometodo.validation.constraint.LowerCaseString;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Locale;

public class LowerCaseStringValidator implements ConstraintValidator<LowerCaseString,String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if(value==null)
            return true;

        boolean isReceivedValueLowerCase=value.toLowerCase(Locale.ROOT).equals(value);
        return isReceivedValueLowerCase;

    }
}
