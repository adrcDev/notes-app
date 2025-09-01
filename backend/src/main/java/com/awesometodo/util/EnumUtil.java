package com.awesometodo.util;

import java.util.Optional;

public class EnumUtil {

    private EnumUtil() {}

    public static Optional<String> convertToSpaceSeparatedLowerCaseString(Enum<?> enumValue) {
        if (enumValue==null) {
            return Optional.empty();
        }

        String result = enumValue.name().toLowerCase().replace('_', ' ');
        return Optional.of(result);
    }

    public static <E extends Enum<E>> Optional<E> convertStringToSpecifiedEnumClassConstant(String string, Class<E> enumClass) {
        if(string==null || enumClass==null)
            return Optional.empty();

        try {
            return Optional.of(Enum.valueOf(enumClass, string.toUpperCase().replace(' ','_')));
        } catch(IllegalArgumentException e) {
            return Optional.empty();
        }
    }
}
