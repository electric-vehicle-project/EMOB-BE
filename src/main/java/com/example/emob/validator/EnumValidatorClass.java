package com.example.emob.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;
import java.util.Objects;

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
            isValid = Arrays.stream(enumClass.getEnumConstants())
                    .anyMatch(e -> e.name().equals(value.toString()));
        }

        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    (message == null || message.isEmpty())
                            ? "Invalid value for enum " + enumClass.getSimpleName()
                            : message
            ).addConstraintViolation();
        }

        return isValid;
    }
}
