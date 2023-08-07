package com.example.sns.jwt;

import com.example.sns.global.dto.ResponseDto;
import com.example.sns.jwt.exception.CustomJwtException;
import com.example.sns.jwt.exception.JwtExceptionType;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Instant;
import java.util.Date;

@Slf4j
@Component
public class JwtUtils {
    private final Key signingKey;
    private final JwtParser jwtParser;

    public JwtUtils(@Value("${jwt.secret}") String jwtSecret) {
        this.signingKey = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        this.jwtParser = Jwts
                .parserBuilder()
                .setSigningKey(this.signingKey)
                .build();
    }

    public JwtDto generateToken(String username) {
        Claims jwtClaims = Jwts.claims()
                .setSubject(username)
                .setIssuedAt(Date.from(Instant.now()))
                .setExpiration(Date.from(Instant.now().plusSeconds(3600)));
        JwtDto jwt = new JwtDto();
        jwt.setToken(
                Jwts.builder()
                        .setClaims(jwtClaims)
                        .signWith(signingKey)
                        .compact()
        );
        jwt.setStatus(HttpStatus.OK);
        return jwt;
    }

    public void validate(String authHeader) throws CustomJwtException {
        // header 가 빈 경우
        if (authHeader == null) {
            log.error("AuthHeader 가 빈 상태");
            throw new CustomJwtException(JwtExceptionType.NULL_TOKEN_ERROR);
        }
        // Bearer token 이 아닌 경우
        if (!authHeader.startsWith("Bearer ")) {
            log.error("지원되지 않는 토큰 타입");
            throw new CustomJwtException(JwtExceptionType.TOKEN_TYPE_ERROR);
        }
        try {
            String token = authHeader.split(" ")[1];
            jwtParser.parseClaimsJws(token);
        } catch (SignatureException ex) {
            log.error("서명이 유효하지 않음");
            throw new CustomJwtException(JwtExceptionType.JWT_SIGNATURE_ERROR);
        } catch (MalformedJwtException ex) {
            log.error("JWT 의 형식이 올바르지 않음");
            throw new CustomJwtException(JwtExceptionType.JWT_MALFORMED_ERROR);
        } catch (ExpiredJwtException ex) {
            log.error("JWT 의 유효시간이 만료");
            throw new CustomJwtException(JwtExceptionType.JWT_EXPIRED_ERROR);
        } catch (UnsupportedJwtException ex) {
            log.error("지원되지 않는 기능이 사용됨");
            throw new CustomJwtException(JwtExceptionType.UNSUPPORTED_JWT_ERROR);
        } catch (IllegalArgumentException ex) {
            log.error("JWT 의 내용이 빈 상태");
            throw new CustomJwtException(JwtExceptionType.ILLEGAL_ARGUMENT_JWT_ERROR);
        }
    }

    public String getUsernameFromJwt(String token) {
        return jwtParser
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}
