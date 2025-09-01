package com.awesometodo.entity.converter;

import com.awesometodo.entity.SignupOtp;
import com.awesometodo.util.EnumUtil;
import jakarta.persistence.AttributeConverter;

public class OtpTypeToStringConverter implements AttributeConverter<SignupOtp.OtpType,String> {

    @Override
    public String convertToDatabaseColumn(SignupOtp.OtpType attribute) {
        return EnumUtil.convertToSpaceSeparatedLowerCaseString(attribute).get();
    }

    @Override
    public SignupOtp.OtpType convertToEntityAttribute(String dbData) {
        return EnumUtil.convertStringToSpecifiedEnumClassConstant(dbData,SignupOtp.OtpType.class).get();
    }
}
