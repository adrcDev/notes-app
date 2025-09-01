package com.awesometodo.validation.validator;

import com.awesometodo.validation.constraint.PhoneNumberE164;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.Phonenumber;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import com.google.i18n.phonenumbers.PhoneNumberUtil;


public class PhoneNumberE164Validator implements ConstraintValidator<PhoneNumberE164,String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if(value==null)
            return true;


        String e164FormatRegex="^\\+\\d{1,15}$";
        if(!value.matches(e164FormatRegex))
            return false;

        PhoneNumberUtil phoneNumberUtil=PhoneNumberUtil.getInstance();
        Phonenumber.PhoneNumber phoneNumber;
        try {
            phoneNumber = phoneNumberUtil.parse(value,null);
        } catch (NumberParseException e) {
            return false;
        }

        boolean isReceivedPhoneNumberValid=phoneNumberUtil.isValidNumber(phoneNumber);

        return isReceivedPhoneNumberValid;
    }
}
