/* EMOB-2025 */
package com.example.emob.exception;

import com.example.emob.constant.ErrorCode;
import com.example.emob.model.response.APIResponse;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<APIResponse<Object>> handleValidation(
            MethodArgumentNotValidException exception) {
        APIResponse<Object> apiResponse = new APIResponse<>();

        String field = Objects.requireNonNull(exception.getFieldError()).getField();
        log.info(field);
        ErrorCode errorCode;

        switch (field) {
            case "email":
                errorCode = ErrorCode.INVALID_EMAIL;
                break;
            case "phone":
                errorCode = ErrorCode.INVALID_PHONE_NUMBER;
                break;
            case "fullName":
                errorCode = ErrorCode.FULL_NAME_REQUIRED;
                break;
            case "password":
                errorCode = ErrorCode.PASSWORD_TOO_SHORT;
                break;
            default:
                errorCode = ErrorCode.INVALID_CODE;
        }

        apiResponse.setCode(errorCode.getCode());
        apiResponse.setMessage(errorCode.getMessage());

        return ResponseEntity.status(errorCode.getStatus()).body(apiResponse);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<APIResponse<Object>> handleAccessDinedException(
            AccessDeniedException exception) {
        ErrorCode errorCode = ErrorCode.UNAUTHORIZED;
        APIResponse<Object> apiResponse = new APIResponse<>();
        apiResponse.setCode(errorCode.getCode());
        apiResponse.setMessage(errorCode.getMessage());

        return ResponseEntity.status(errorCode.getStatus()).body(apiResponse);
    }

    @ExceptionHandler(GlobalException.class)
    public ResponseEntity<APIResponse<Object>> globalException(GlobalException exception) {
        ErrorCode errorCode = exception.getErrorCode();
        APIResponse<Object> apiResponse = new APIResponse<>();
        apiResponse.setCode(errorCode.getCode());
        apiResponse.setMessage(errorCode.getMessage());

        return ResponseEntity.status(errorCode.getStatus()).body(apiResponse);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<APIResponse<Object>> httpMessageNotReadableException(
            HttpMessageNotReadableException exception) {
        APIResponse<Object> apiResponse = new APIResponse<>();
        apiResponse.setCode(ErrorCode.NOT_FOUND_ENUM.getCode());
        apiResponse.setMessage(ErrorCode.NOT_FOUND_ENUM.getMessage());
        return ResponseEntity.status(ErrorCode.NOT_FOUND_ENUM.getStatus()).body(apiResponse);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<APIResponse<Object>> handeIllegalArgumentException(
            IllegalArgumentException exception) {
        APIResponse<Object> apiResponse = new APIResponse<>();
        apiResponse.setCode(ErrorCode.FIELDS_EMPTY.getCode());
        apiResponse.setMessage(ErrorCode.FIELDS_EMPTY.getMessage());
        return ResponseEntity.status(ErrorCode.FIELDS_EMPTY.getStatus()).body(apiResponse);
    }
}
