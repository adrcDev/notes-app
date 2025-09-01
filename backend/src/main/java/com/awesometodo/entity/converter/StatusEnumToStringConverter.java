package com.awesometodo.entity.converter;

import com.awesometodo.entity.JwtRefreshToken;
import com.awesometodo.util.EnumUtil;
import jakarta.persistence.AttributeConverter;

public class StatusEnumToStringConverter implements AttributeConverter<JwtRefreshToken.Status,String> {
    @Override
    public String convertToDatabaseColumn(JwtRefreshToken.Status attribute) {
        return EnumUtil.convertToSpaceSeparatedLowerCaseString(attribute).get();
    }

    @Override
    public JwtRefreshToken.Status convertToEntityAttribute(String dbData) {
        return EnumUtil.convertStringToSpecifiedEnumClassConstant(dbData, JwtRefreshToken.Status.class).get();
    }
}
