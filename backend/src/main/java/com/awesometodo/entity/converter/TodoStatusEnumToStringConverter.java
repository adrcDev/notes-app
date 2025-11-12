package com.awesometodo.entity.converter;

import com.awesometodo.entity.Todo;
import com.awesometodo.entity.enums.Gender;
import com.awesometodo.util.EnumUtil;
import jakarta.persistence.AttributeConverter;

public class TodoStatusEnumToStringConverter implements AttributeConverter<Todo.Status,String> {
    @Override
    public String convertToDatabaseColumn(Todo.Status attribute) {
        return EnumUtil.convertToSpaceSeparatedLowerCaseString(attribute).get();
    }

    @Override
    public Todo.Status convertToEntityAttribute(String dbData) {
        return EnumUtil.convertStringToSpecifiedEnumClassConstant(dbData, Todo.Status.class).get();
    }
}
