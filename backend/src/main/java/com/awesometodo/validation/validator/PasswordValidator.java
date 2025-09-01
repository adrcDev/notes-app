package com.awesometodo.validation.validator;

import com.awesometodo.validation.constraint.Password;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.text.Normalizer;
import java.util.regex.Pattern;

public class PasswordValidator implements ConstraintValidator<Password,String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if(value==null)
            return true;

        String normalizedUnicodeString=Normalizer.normalize(value, Normalizer.Form.NFC);
        String passwordStructureRegex="^\\X{8,128}$";
        String noWhiteSpaceCharactersRegex="^[^\\s]+$";
        boolean isValidPassword=normalizedUnicodeString.matches(passwordStructureRegex) && normalizedUnicodeString.matches(noWhiteSpaceCharactersRegex);
        return isValidPassword;

    }
}
