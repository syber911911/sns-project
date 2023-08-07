package com.example.sns.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UserLoginDto {
    @NotBlank(message = "아이디 입력은 필수입니다.")
    private String username;
    @NotBlank(message = "비밀번호 입력은 필수입니다.")
    private String password;
}
