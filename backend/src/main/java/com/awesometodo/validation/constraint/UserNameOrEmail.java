package com.awesometodo.validation.constraint;

import com.awesometodo.validation.validator.UserNameOrEmailValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD,ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UserNameOrEmailValidator.class)
public @interface UserNameOrEmail {

    String message() default "{com.awesometodo.validation.constraint.UserNameOrEmail." +
            "message}";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

}
