package com.awesometodo.validation.constraint;

import com.awesometodo.validation.validator.DateOfBirthValidator;
import com.awesometodo.validation.validator.NotEmptyStringValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD,ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NotEmptyStringValidator.class)
public @interface NotEmptyString {
    String message() default "{com.awesometodo.validation.constraint.NotEmptyString." +
            "message}";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
}
