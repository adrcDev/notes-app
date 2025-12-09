package com.awesometodo.validation.validator;

import com.awesometodo.validation.constraint.ValidQuillDelta;
import com.awesometodo.validation.util.QuillDeltaValidator;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidQuillDeltaValidator implements ConstraintValidator<ValidQuillDelta,String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if(value==null)
            return true;

        return QuillDeltaValidator.isValid(value);
    }
}
