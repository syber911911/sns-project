package com.example.sns.jwt;

import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class JwtDto {
    private String token;
    private HttpStatus status;
}
