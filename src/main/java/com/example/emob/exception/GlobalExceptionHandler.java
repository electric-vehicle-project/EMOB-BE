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

    String enumkey = exception.getFieldError().getDefaultMessage();

    ErrorCode errorCode;

    try {
      errorCode = ErrorCode.valueOf(enumkey);

    } catch (IllegalArgumentException e) {
      errorCode = ErrorCode.INVALID_CODE;
    }

    apiResponse.setCode(errorCode.getCode());

    switch (errorCode) {
      case FIELD_REQUIRED -> {
        String field = Objects.requireNonNull(exception.getFieldError()).getField();
        String message = field + " " + errorCode.getMessage();
        apiResponse.setMessage(message);
      }
      case INVALID_SIZE_100 -> {
        var fieldError = exception.getBindingResult().getFieldError();
        String field = (fieldError != null) ? fieldError.getField() : "unknown field";
        String message = field + " " + errorCode.getMessage();
        apiResponse.setMessage(message);
      }
      case INVALID_MIN_0 -> {
        var fieldError = exception.getBindingResult().getFieldError();
        String field = (fieldError != null) ? fieldError.getField() : "unknown field";
        String message = field + " " + errorCode.getMessage();
        apiResponse.setMessage(message);
      }
      default -> apiResponse.setMessage(errorCode.getMessage());
    }

    return ResponseEntity.status(errorCode.getCode()).body(apiResponse);
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
    apiResponse.setMessage(exception.getMessage());

    return ResponseEntity.status(errorCode.getStatus()).body(apiResponse);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<APIResponse<Object>> httpMessageNotReadableException(
      HttpMessageNotReadableException exception) {
    APIResponse<Object> apiResponse = new APIResponse<>();
    String message = exception.getMessage();

    if (message != null && message.contains("values accepted for Enum class")) {
      int start = message.indexOf("values accepted for Enum class");
      message = message.substring(start);
    }

    // Làm sạch thêm (xóa dấu chấm thừa hoặc dòng mới)
    message = message.replaceAll("\\s+", " ").trim();
    apiResponse.setCode(ErrorCode.NOT_FOUND_ENUM.getCode());
    log.info(exception.getMessage());
    apiResponse.setMessage(message);
    return ResponseEntity.status(ErrorCode.NOT_FOUND_ENUM.getStatus()).body(apiResponse);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<APIResponse<Object>> handeIllegalArgumentException(
      IllegalArgumentException exception) {
    APIResponse<Object> apiResponse = new APIResponse<>();
    apiResponse.setCode(ErrorCode.FIELDS_EMPTY.getCode());
    apiResponse.setMessage(exception.getMessage());
    return ResponseEntity.status(ErrorCode.FIELDS_EMPTY.getStatus()).body(apiResponse);
  }
}
