package com.awesometodo.validation.constraint;

import com.awesometodo.validation.validator.PhoneNumberE164Validator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD,ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PhoneNumberE164Validator.class)
public @interface PhoneNumberE164 {
    String message() default "{com.awesometodo.validation.constraint.PhoneNumber." +
            "message}";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
}
