package com.awesometodo.validation.validator;

import com.awesometodo.validation.constraint.Username;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class UsernameValidator implements ConstraintValidator<Username,String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if(value==null) {
            return true;
        }

        return isValidUserName(value,context);
    }

    private boolean isValidUserName(String value,ConstraintValidatorContext context) {
        String regexForPartiallyValidUserName="^[a-zA-Z0-9][a-zA-Z0-9._-]{1,28}[a-zA-Z0-9]$";
        boolean isPartiallyValidUserName=value.matches(regexForPartiallyValidUserName);
        if(!isPartiallyValidUserName) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("The received username did not match the regex:- ^[a-zA-Z0-9][a-zA-Z0-9._-]{1,28}[a-zA-Z0-9]$").addConstraintViolation();
            return false;
        }

        if(isStringContainsConsecutiveSpecialChar(value)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("The received username contains consecutive special characters. (allowed special characters:- .(dot), _(underscore), -(hyphen)").addConstraintViolation();
            return false;
        }
        else {
            return true;
        }
    }

    private boolean isStringContainsConsecutiveSpecialChar(String value) {
        if(value.contains("..") || value.contains("__") || value.contains("--") || value.contains("._") ||      value.contains(".-") || value.contains("_-") || value.contains("_.") || value.contains("-_") || value.contains("-.")) {
            return true;
        }
        else {
            return false;
        }
    }
}
