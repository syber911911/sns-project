package com.example.sns.user.dto;

import com.example.sns.user.entity.UserEntity;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Builder
@Getter
public class CustomUserDetails implements UserDetails {
    private String username;
    private String password;
    private String realName;
    private String email;
    private String phone;
    private String address;
    private String profileImg;
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public static CustomUserDetails fromEntity(UserEntity userEntity) {
        return CustomUserDetails.builder()
                .username(userEntity.getUsername())
                .password(userEntity.getPassword())
                .realName(userEntity.getRealName())
                .email(userEntity.getEmail())
                .phone(userEntity.getPhone())
                .address(userEntity.getAddress())
                .profileImg(userEntity.getProfileImg())
                .build();
    }

    public static CustomUserDetails fromDto(UserRegisterDto userRegisterDto) {
        return CustomUserDetails.builder()
                .username(userRegisterDto.getUsername())
                .password(userRegisterDto.getPassword())
                .realName(userRegisterDto.getRealName())
                .email(userRegisterDto.getEmail())
                .phone(userRegisterDto.getPhone())
                .address(userRegisterDto.getAddress())
                .build();
    }
}
