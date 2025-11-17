package com.awesometodo.validation.validator;

import com.awesometodo.validation.constraint.DateStringYYYYMMDD;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class DateStringYYYYMMDDValidator implements ConstraintValidator<DateStringYYYYMMDD,String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if(value==null)
            return true;

        try {
            LocalDate.parse(value);
            return true;
        } catch(DateTimeParseException e) {
            return false;
        }
    }

}
