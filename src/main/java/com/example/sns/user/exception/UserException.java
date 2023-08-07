package com.example.sns.user.exception;

import com.example.sns.global.exception.BaseException;
import com.example.sns.global.exception.BaseExceptionType;

public class UserException extends BaseException {
    private final BaseExceptionType exceptionType;

    public UserException(BaseExceptionType exceptionType) {
        this.exceptionType = exceptionType;
    }

    @Override
    public BaseExceptionType getExceptionType() {
        return this.exceptionType;
    }
}
