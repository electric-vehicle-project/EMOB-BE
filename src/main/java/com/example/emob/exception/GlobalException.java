package com.example.emob.exception;

import com.example.emob.constant.ErrorCode;

public class GlobalException extends RuntimeException {
    ErrorCode errorCode;
    public GlobalException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
