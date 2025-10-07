<<<<<<< HEAD
=======
/* EMOB-2025 */
>>>>>>> f514e41d121209766b1808e639b623d8b269ae3d
package com.example.emob.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
<<<<<<< HEAD

import java.util.Arrays;
import java.util.Objects;
=======
import java.util.Arrays;
>>>>>>> f514e41d121209766b1808e639b623d8b269ae3d

// ?: wildcard => Enum<?> chỉ 1 enum bât kì ....
public class EnumValidatorClass implements ConstraintValidator<EnumValidator, Object> {
    private Class<? extends Enum<?>> enumClass;
    private String message;

    @Override
    public void initialize(EnumValidator constraintAnnotation) {
        this.enumClass = constraintAnnotation.enumClass();
        this.message = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) return true;

        boolean isValid;
        if (value instanceof Enum<?>) {
            isValid = Arrays.asList(enumClass.getEnumConstants()).contains(value);
        } else {
            // so sánh String hoặc Integer
<<<<<<< HEAD
            isValid = Arrays.stream(enumClass.getEnumConstants())
                    .anyMatch(e -> e.name().equals(value.toString()));
=======
            isValid =
                    Arrays.stream(enumClass.getEnumConstants())
                            .anyMatch(e -> e.name().equals(value.toString()));
>>>>>>> f514e41d121209766b1808e639b623d8b269ae3d
        }

        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
<<<<<<< HEAD
                    (message == null || message.isEmpty())
                            ? "Invalid value for enum " + enumClass.getSimpleName()
                            : message
            ).addConstraintViolation();
=======
                            (message == null || message.isEmpty())
                                    ? "Invalid value for enum " + enumClass.getSimpleName()
                                    : message)
                    .addConstraintViolation();
>>>>>>> f514e41d121209766b1808e639b623d8b269ae3d
        }

        return isValid;
    }
}
