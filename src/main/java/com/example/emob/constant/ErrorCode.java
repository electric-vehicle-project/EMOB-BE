package com.example.emob.constant;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
public enum ErrorCode {
    EMAIL_EXISTED("Email is existed", HttpStatus.BAD_REQUEST),
    PHONE_EXISTED("Phone is existed", HttpStatus.BAD_REQUEST),
    INVALID_CREDENTIALS("Username or password invalid!", HttpStatus.BAD_REQUEST),
    INVALID_PHONE_NUMBER("Invalid phone number!", HttpStatus.BAD_REQUEST),
    INVALID_EMAIL("Invalid email address!", HttpStatus.BAD_REQUEST),
    FULL_NAME_REQUIRED("Full name cannot be blank!", HttpStatus.BAD_REQUEST),
    PASSWORD_TOO_SHORT("Password must be at least 6 characters long!", HttpStatus.BAD_REQUEST),
    EMPTY_TOKEN("Empty token", HttpStatus.UNAUTHORIZED),
    EXPIRED_TOKEN("Expired token!", HttpStatus.UNAUTHORIZED),
    INVALID_TOKEN("Invalid token!", HttpStatus.UNAUTHORIZED),
    INVALID_CODE("Invalid message code!", HttpStatus.BAD_REQUEST),
    NOT_MATCH_TOKEN("Token does not match locally computed signature!", HttpStatus.UNAUTHORIZED),
    OTHER(null, HttpStatus.BAD_REQUEST),
    UNCATEGORIZED_EXCEPTION(null, HttpStatus.INTERNAL_SERVER_ERROR),
    UNAUTHENTICATED("Unauthenticated!", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED("You do not have permission!", HttpStatus.FORBIDDEN),
    DATA_INVALID( "Invalid data", HttpStatus.BAD_REQUEST),
    DB_ERROR("Database error", HttpStatus.INTERNAL_SERVER_ERROR),
    NOT_FOUND("Not found!", HttpStatus.NOT_FOUND);

    final String message;
    final HttpStatus status;

    public int getCode() {
        return status.value();
    }
}

