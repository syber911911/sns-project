package com.example.sns.user.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@ToString
@Setter
@Getter
public class UserRegisterDto {

    @NotNull(message = "아이디 입력은 필수입니다.")
    @Pattern(regexp = "[a-z][a-z0-9]{5,14}$", message = "아이디는 소문자로만 시작 가능합니다 | 사용 가능 문자 : 영소문자, 숫자 | 글자수 제한 : 최소 6자에서 15자)")
    private String username;

    @NotNull(message = "비밀번호 입력은 필수입니다.")
    @Pattern(regexp = "^(?=.*[a-zA-z])(?=.*[0-9])(?=.*[`~₩!@#$%^&*]).{8,20}$", message = "비밀번호는 영문 숫자 특수문자를 모두 포함해야 합니다 | 사용 가능 문자 : 영대소문자, 숫자, 특수문자 (`~₩!@#$%^&*) | 글자수 제한 : 최소 8자에서 20자")
    private String password;

    @NotNull(message = "확인용 비밀번호 입력은 필수입니다.")
    private String passwordCheck;

    @Min(value = 2, message = "2자 이상의 이름을 입력해주세요.")
    private String realName;

    @Pattern(regexp = "^[a-zA-Z0-9+-_.]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$", message = "이메일 형식이 잘못되었습니다.")
    private String email;

    @Pattern(regexp = "^01(?:0|1|[6-9])[.-]?(\\d{3}|\\d{4})[.-]?(\\d{4})$", message = "전화번호 형식이 잘못되었습니다.")
    private String phone;

    private String address;
}
