package com.example.sns.user.controller;

import com.example.sns.global.dto.ResponseDto;
import com.example.sns.jwt.JwtDto;
import com.example.sns.user.dto.CustomUserDetails;
import com.example.sns.user.dto.UserLoginDto;
import com.example.sns.user.dto.UserRegisterDto;
import com.example.sns.user.exception.UserException;
import com.example.sns.user.exception.UserExceptionType;
import com.example.sns.user.service.CustomUserDetailsManager;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/auth")
public class UserController {
    private final UserDetailsManager userDetailsManager;

    public UserController(UserDetailsManager userDetailsManager) {
        this.userDetailsManager = userDetailsManager;
    }

    @PostMapping("/register")
    public ResponseDto register(@RequestBody @Valid UserRegisterDto request) {
        if (!request.getPassword().equals(request.getPasswordCheck()))
            throw new UserException(UserExceptionType.UNMATCHED_CHECK_PASSWORD);
        userDetailsManager.createUser(CustomUserDetails.fromDto(request));
        ResponseDto response = new ResponseDto();
        response.setMessage("회원가입이 완료되었습니다");
        response.setHttpStatus(HttpStatus.OK);
        return response;
    }

    @PostMapping("/login")
    public JwtDto login(@RequestBody @Valid UserLoginDto request) {
        return ((CustomUserDetailsManager) userDetailsManager).loginUser(request);
    }

    @PutMapping(value = "/profile/image", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseDto uploadProfileImage(
            @RequestPart("image") MultipartFile profileImage,
            @AuthenticationPrincipal String username
    ) {
        return ((CustomUserDetailsManager) userDetailsManager).uploadProfileImage(username, profileImage);
    }
}
