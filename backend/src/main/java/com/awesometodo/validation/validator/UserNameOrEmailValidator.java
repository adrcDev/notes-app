package com.awesometodo.validation.validator;

import com.awesometodo.validation.constraint.UserNameOrEmail;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class UserNameOrEmailValidator implements ConstraintValidator<UserNameOrEmail,String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if(value==null)
            return true;

        boolean isValueContainsAtSymbol=value.contains("@");
        if(isValueContainsAtSymbol)
            return isValidEmail(value);
        else
            return isValidUserName(value);
    }

    private boolean isValidEmail(String value) {
        String regexForValidEmail="^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return value.matches(regexForValidEmail);
    }

    private boolean isValidUserName(String value) {
        String regexForPartiallyValidUserName="^[a-zA-Z0-9][a-zA-Z0-9._-]{1,28}[a-zA-Z0-9]$";
        boolean isPartiallyValidUserName=value.matches(regexForPartiallyValidUserName);
        if(!isPartiallyValidUserName)
            return false;

        if(isStringContainsConsecutiveSpecialChar(value))
            return false;
        else
            return true;
    }



    private boolean isStringContainsConsecutiveSpecialChar(String value) {
        if(value.contains("..") || value.contains("__") || value.contains("--") || value.contains("._") ||      value.contains(".-") || value.contains("_-") || value.contains("_.") || value.contains("-_") || value.contains("-."))
            return true;
        else
            return false;
    }


}
