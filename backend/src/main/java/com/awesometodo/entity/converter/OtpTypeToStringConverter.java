package com.awesometodo.entity.converter;

import com.awesometodo.entity.SignupOtp;
import com.awesometodo.entity.enums.OtpType;
import com.awesometodo.util.EnumUtil;
import jakarta.persistence.AttributeConverter;

public class OtpTypeToStringConverter implements AttributeConverter<OtpType,String> {

    @Override
    public String convertToDatabaseColumn(OtpType attribute) {
        return EnumUtil.convertToSpaceSeparatedLowerCaseString(attribute).get();
    }

    @Override
    public OtpType convertToEntityAttribute(String dbData) {
        return EnumUtil.convertStringToSpecifiedEnumClassConstant(dbData,OtpType.class).get();
    }
}
