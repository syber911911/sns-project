package com.example.sns.jwt.exception;

import com.example.sns.global.exception.BaseException;
import com.example.sns.global.exception.BaseExceptionType;
import io.jsonwebtoken.JwtException;

public class CustomJwtException extends BaseException {
    JwtExceptionType jwtExceptionType;

    public CustomJwtException(JwtExceptionType jwtExceptionType) {
        this.jwtExceptionType = jwtExceptionType;
    }

    @Override
    public BaseExceptionType getExceptionType() {
        return this.jwtExceptionType;
    }
}
