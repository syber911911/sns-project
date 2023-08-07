package com.example.sns.jwt;

import com.example.sns.global.dto.ResponseDto;
import com.example.sns.jwt.exception.CustomJwtException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
public class JwtFilter extends OncePerRequestFilter {
    private final JwtUtils jwtUtils;

    public JwtFilter(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException, CustomJwtException {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        try {
            jwtUtils.validate(authHeader);
            String token = authHeader.split(" ")[1];
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(new UsernamePasswordAuthenticationToken(jwtUtils.getUsernameFromJwt(token), token, null));
            SecurityContextHolder.setContext(context);
            log.info("set security with jwt");
            filterChain.doFilter(request, response);
        } catch (CustomJwtException ex) {
            this.setErrorMessage(response, ex.getExceptionType().getHttpStatus(), ex.getExceptionType().getErrorMessage());
        }
    }

    public void setErrorMessage(HttpServletResponse response, HttpStatus httpStatus, String message) {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.setStatus(httpStatus.value());
        ObjectMapper objectMapper = new ObjectMapper();
        ResponseDto responseDto = new ResponseDto();
        responseDto.setHttpStatus(httpStatus);
        responseDto.setMessage(message);
        try {
            response.getWriter().write(objectMapper.writeValueAsString(responseDto));
        } catch (Exception ex) {
            log.warn("fail error message convert to json");
        }
    }
}
