package com.awesometodo.entity.converter;

import com.awesometodo.entity.Todo;
import com.awesometodo.entity.enums.Gender;
import com.awesometodo.util.EnumUtil;
import jakarta.persistence.AttributeConverter;

public class TodoPriorityEnumToStringConverter implements AttributeConverter<Todo.Priority,String> {
    @Override
    public String convertToDatabaseColumn(Todo.Priority attribute) {
        return EnumUtil.convertToSpaceSeparatedLowerCaseString(attribute).get();
    }

    @Override
    public Todo.Priority convertToEntityAttribute(String dbData) {
        return EnumUtil.convertStringToSpecifiedEnumClassConstant(dbData,Todo.Priority.class).get();
    }
}
