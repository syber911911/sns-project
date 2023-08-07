package com.example.sns.global.dto;

import lombok.Data;
import org.springframework.http.HttpStatus;

import java.util.List;

@Data
public class ValidationResponseDto {
    private List<String> messageList;
    private HttpStatus statusCode;
}
