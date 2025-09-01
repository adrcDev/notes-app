package com.awesometodo.entity.converter;

import com.awesometodo.entity.enums.Gender;
import com.awesometodo.util.EnumUtil;
import jakarta.persistence.AttributeConverter;


public class GenderEnumToStringConverter implements AttributeConverter<Gender,String> {
    @Override
    public String convertToDatabaseColumn(Gender attribute) {
        return EnumUtil.convertToSpaceSeparatedLowerCaseString(attribute).get();
    }

    @Override
    public Gender convertToEntityAttribute(String dbData) {
        return EnumUtil.convertStringToSpecifiedEnumClassConstant(dbData,Gender.class).get();
    }


}
