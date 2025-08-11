package com.awesometodo.converter;

import com.awesometodo.entity.JwtRefreshToken;
import com.awesometodo.entity.User;
import jakarta.persistence.AttributeConverter;

public class StatusEnumToStringConverter implements AttributeConverter<JwtRefreshToken.Status,String> {
    @Override
    public String convertToDatabaseColumn(JwtRefreshToken.Status attribute) {
        boolean isMultiWordEnum=attribute.name().contains("_");
        if(isMultiWordEnum) {
            String spaceSeparatedLowerCaseString=attribute.name().toLowerCase().replace('_',' ');
            return spaceSeparatedLowerCaseString;
        }
        else {
            return attribute.name().toLowerCase();
        }
    }

    @Override
    public JwtRefreshToken.Status convertToEntityAttribute(String dbData) {
        boolean isDbDataMultiWord=dbData.contains(" ");
        if(isDbDataMultiWord) {
            String genderUpperCase=dbData.toUpperCase();
            return JwtRefreshToken.Status.valueOf(genderUpperCase.replace(' ','_'));
        }
        else {
            return JwtRefreshToken.Status.valueOf(dbData.toUpperCase());
        }
    }
}
