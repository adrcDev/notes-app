package com.awesometodo.validation.constraint;

import com.awesometodo.validation.validator.ValidTodoDueDateRangeValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE,ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidTodoDueDateRangeValidator.class)
public @interface ValidTodoDueDateRange {
    String message() default "{com.awesometodo.validation.constraint.ValidDueDateRange." +
            "message}";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
}
