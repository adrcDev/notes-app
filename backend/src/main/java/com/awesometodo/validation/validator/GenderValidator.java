package com.awesometodo.validation.validator;

import com.awesometodo.util.EnumUtil;
import com.awesometodo.validation.constraint.Gender;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class GenderValidator implements ConstraintValidator<Gender,String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if(value==null)
            return true;

        com.awesometodo.entity.enums.Gender[] allGenderEnumConstants=com.awesometodo.entity.enums.Gender.values();
        for(com.awesometodo.entity.enums.Gender gender : allGenderEnumConstants) {
            String genderEnumConstantAsString=EnumUtil.convertToSpaceSeparatedLowerCaseString(gender).get();
            if(value.equals(genderEnumConstantAsString))
                return true;
        }

        return false;

    }
}
