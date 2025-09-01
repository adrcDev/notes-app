package com.awesometodo.validation.validator;

import com.awesometodo.validation.constraint.DateOfBirth;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class DateOfBirthValidator implements ConstraintValidator<DateOfBirth,String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if(value==null)
            return true;

        String regexForYYYYMMDDFormat="^\\d{4}-\\d{2}-\\d{2}$";
        boolean isValueInYYYYMMDDFormat=value.matches(regexForYYYYMMDDFormat);

        if(!isValueInYYYYMMDDFormat)
            return false;

        if(!isValuesInYYYYMMDDFormatRepresentValidDate(value))
            return false;

        LocalDate todaysDate=LocalDate.now();
        LocalDate receivedDate=LocalDate.parse(value);
        boolean isReceivedDateEarlierOrEqualToTodaysDate=receivedDate.compareTo(todaysDate)<=0;
        boolean isPersonAgeLessThanOrEqualTo160=receivedDate.getYear()>=(todaysDate.getYear()-160);

        if(isReceivedDateEarlierOrEqualToTodaysDate && isPersonAgeLessThanOrEqualTo160)
            return true;
        else
            return false;
    }


    private boolean isValuesInYYYYMMDDFormatRepresentValidDate(String value) {
        try {
            LocalDate.parse(value);
            return true;
        } catch(DateTimeParseException e) {
            return false;
        }

    }


}
