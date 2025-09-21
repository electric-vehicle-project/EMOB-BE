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
    /* =============== ERROR_ENUM ================ */
    EMAIL_EXISTED(org.apache.http.HttpStatus.SC_BAD_REQUEST, "Email is existed", HttpStatus.BAD_REQUEST), // 400
    PHONE_EXISTED(org.apache.http.HttpStatus.SC_BAD_REQUEST, "Phone is existed", HttpStatus.BAD_REQUEST), // 400
    INVALID_CREDENTIALS(org.apache.http.HttpStatus.SC_BAD_REQUEST, "Username or password invalid!",HttpStatus.BAD_REQUEST), // 400
    INVALID_PHONE_NUMBER(org.apache.http.HttpStatus.SC_BAD_REQUEST, "Invalid phone number!",HttpStatus.BAD_REQUEST),
    INVALID_EMAIL(org.apache.http.HttpStatus.SC_BAD_REQUEST, "Invalid email address!",HttpStatus.BAD_REQUEST),
    FULL_NAME_REQUIRED(org.apache.http.HttpStatus.SC_BAD_REQUEST, "Full name cannot be blank!",HttpStatus.BAD_REQUEST),
    PASSWORD_TOO_SHORT(org.apache.http.HttpStatus.SC_BAD_REQUEST, "Password must be at least 6 characters long!",HttpStatus.BAD_REQUEST),
    EMPTY_TOKEN(org.apache.http.HttpStatus.SC_UNAUTHORIZED, "Empty token", HttpStatus.UNAUTHORIZED), // 401
    EXPIRED_TOKEN(org.apache.http.HttpStatus.SC_UNAUTHORIZED, "Expired token!",HttpStatus.UNAUTHORIZED),
    INVALID_TOKEN(org.apache.http.HttpStatus.SC_UNAUTHORIZED, "Invalid token!",HttpStatus.UNAUTHORIZED),
    INVALID_CODE(org.apache.http.HttpStatus.SC_BAD_REQUEST, "Invalid message code!",HttpStatus.BAD_REQUEST),
    NOT_MATCH_TOKEN(org.apache.http.HttpStatus.SC_UNAUTHORIZED, "Token does not match locally computed signature!",HttpStatus.UNAUTHORIZED),
    OTHER(org.apache.http.HttpStatus.SC_BAD_REQUEST, null,HttpStatus.BAD_REQUEST),
    UNCATEGORIZED_EXCEPTION(org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR, null,HttpStatus.INTERNAL_SERVER_ERROR), // 500
    UNAUTHENTICATED(org.apache.http.HttpStatus.SC_UNAUTHORIZED, "Unauthenticated!",HttpStatus.UNAUTHORIZED), // 401: xác thực
    UNAUTHORIZED(org.apache.http.HttpStatus.SC_FORBIDDEN, "You do not have permission!",HttpStatus.FORBIDDEN), // 403: phân quyền
    NOT_FOUND(org.apache.http.HttpStatus.SC_NOT_FOUND, "Not found!",HttpStatus.NOT_FOUND),; // 404: truy cập
    /* =========================================== */
    final int code;
    final String message;
    final HttpStatus status;
}