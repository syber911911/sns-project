package com.example.sns.global.exception;

import com.example.sns.global.dto.ResponseDto;
import com.example.sns.global.dto.ValidationResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class ExceptionAdvice {
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ResponseDto> handlerBaseException(BaseException ex) {
        ResponseDto response = new ResponseDto();
        response.setHttpStatus(ex.getExceptionType().getHttpStatus());
        response.setMessage(ex.getExceptionType().getErrorMessage());
        return new ResponseEntity<>(response, ex.getExceptionType().getHttpStatus());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationResponseDto> handlerValidationException(MethodArgumentNotValidException ex) {
        ValidationResponseDto response = new ValidationResponseDto();
        List<ObjectError> errors = ex.getBindingResult().getAllErrors();
        List<String> messageList = new ArrayList<>();
        for (ObjectError error : errors) {
            messageList.add(error.getDefaultMessage());
        }
        response.setMessageList(messageList);
        response.setStatusCode(HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
