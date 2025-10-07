<<<<<<< HEAD
=======
/* EMOB-2025 */
>>>>>>> f514e41d121209766b1808e639b623d8b269ae3d
package com.example.emob.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
<<<<<<< HEAD

import java.lang.annotation.*;

@Target({ElementType.FIELD,ElementType.METHOD})
=======
import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.METHOD})
>>>>>>> f514e41d121209766b1808e639b623d8b269ae3d
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {EnumValidatorClass.class})
public @interface EnumValidator {

    Class<? extends Enum<?>> enumClass();

    String message() default "Enum is not match";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
