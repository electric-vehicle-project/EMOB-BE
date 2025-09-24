package com.example.emob.exception;

import com.example.emob.constant.ErrorCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GlobalException extends RuntimeException {
    ErrorCode errorCode;
    public GlobalException(ErrorCode errorCode, String emailCannotBeEmpty) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
