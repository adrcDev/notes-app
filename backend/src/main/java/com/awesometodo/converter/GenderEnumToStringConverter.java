package com.awesometodo.converter;

import com.awesometodo.entity.User;
import jakarta.persistence.AttributeConverter;


public class GenderEnumToStringConverter implements AttributeConverter<User.Gender,String> {
    @Override
    public String convertToDatabaseColumn(User.Gender attribute) {
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
    public User.Gender convertToEntityAttribute(String dbData) {
        boolean isDbDataMultiWord=dbData.contains(" ");
        if(isDbDataMultiWord) {
            String genderUpperCase=dbData.toUpperCase();
            return User.Gender.valueOf(genderUpperCase.replace(' ','_'));
        }
        else {
            return User.Gender.valueOf(dbData.toUpperCase());
        }
    }


}
