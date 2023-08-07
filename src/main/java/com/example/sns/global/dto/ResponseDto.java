package com.example.sns.global.dto;

import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Data
public class ResponseDto {
    private HttpStatus httpStatus;
    private String message;
}
