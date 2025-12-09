package com.awesometodo.validation.constraint;

import com.awesometodo.validation.validator.ValidQuillDeltaValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD,ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidQuillDeltaValidator.class)
public @interface ValidQuillDelta {
    String message() default "{com.awesometodo.validation.constraint.ValidQuillContentDelta." +
            "message}";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
}
