package com.example.sns.user.service;

import com.example.sns.global.dto.ResponseDto;
import com.example.sns.jwt.JwtDto;
import com.example.sns.jwt.JwtUtils;
import com.example.sns.user.dto.CustomUserDetails;
import com.example.sns.user.dto.UserLoginDto;
import com.example.sns.user.entity.UserEntity;
import com.example.sns.user.exception.UserException;
import com.example.sns.user.exception.UserExceptionType;
import com.example.sns.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
public class CustomUserDetailsManager implements UserDetailsManager {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    public CustomUserDetailsManager(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtils jwtUtils) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
    }

    @Override
    public void createUser(UserDetails user) {
        // 중복 아이디 검사
        if (this.userExists(user.getUsername()))
            throw new UserException(UserExceptionType.ALREADY_EXIST_USERNAME);
        // 중복 이메일 검사
        if (((CustomUserDetails) user).getEmail() != null && this.emailExists(((CustomUserDetails) user).getEmail()))
            throw new UserException(UserExceptionType.ALREADY_EXIST_EMAIL);
        // 중복 휴대폰 번호 검사
        if (((CustomUserDetails) user).getEmail() != null && this.phoneExists(((CustomUserDetails) user).getPhone()))
            throw new UserException(UserExceptionType.ALREADY_EXIST_PHONE);
        try {
            userRepository.save(
                    UserEntity.builder()
                            .username(user.getUsername())
                            .password(passwordEncoder.encode(user.getPassword()))
                            .realName(((CustomUserDetails) user).getRealName())
                            .email(((CustomUserDetails) user).getEmail())
                            .phone(((CustomUserDetails) user).getPhone())
                            .address(((CustomUserDetails) user).getAddress())
                            .build()
            );
        } catch (ClassCastException ex) {
            log.error("failed cast to {}", CustomUserDetails.class);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public JwtDto loginUser(UserLoginDto request) {
        UserDetails user = this.loadUserByUsername(request.getUsername());
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword()))
            throw new UserException(UserExceptionType.WRONG_PASSWORD);
        return jwtUtils.generateToken(request.getUsername());
    }

    public ResponseDto uploadProfileImage(String username, MultipartFile profileImage) {
        UserEntity user = this.getUserEntity(username);

        // 사용자 프로필 이미지 저장 디렉토리
        String imageDir = String.format("images/profiles/%s/", username);
        try {
            Files.createDirectories(Path.of(imageDir));
        } catch (IOException ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "디렉토리 생성에 실패");
        }
        // 생성 시간
        LocalDateTime createTime = LocalDateTime.now();

        // 첨부된 이미지 filename 추출
        String originalFileName = profileImage.getOriginalFilename();
        // 확장자 추출
        String[] fileNameSplit = originalFileName.split("\\.");
        String extension = fileNameSplit[fileNameSplit.length - 1];
        // 저장할 이미지의 filename 재설정 (생성시간_username.확장자)
        String profileImageFileName = String.format("%s_%s.%s", createTime.toString(), username, extension);
        // 이미지 저장 경로와 filename 을 합쳐 최종적으로 저장될 path 생성
        String profileImagePath = imageDir + profileImageFileName;

        try {
            // path 에 이미지 저장
            profileImage.transferTo(Path.of(profileImagePath));
        } catch (IOException ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "이미지 업로드 실패");
        }

        // userEntity 에 이미지 경로 추가 및 저장
        user.updateProfileImage(String.format("/profile/%s/%s", username, profileImageFileName));
        userRepository.save(user);

        ResponseDto response = new ResponseDto();
        response.setMessage("프로필 이미지가 업로드 되었습니다");
        response.setHttpStatus(HttpStatus.OK);
        return response;
    }

    @Override
    public void updateUser(UserDetails user) {

    }

    @Override
    public void deleteUser(String username) {

    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {

    }

    @Override
    // 중복되는 아이디 검사
    public boolean userExists(String username) {
        return userRepository.existsByUsername(username);
    }

    // 중복되는 이메일 검사
    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    // 중복되는 휴대폰 번호 검사
    public boolean phoneExists(String phone) {
        return userRepository.existsByPhone(phone);
    }

    public UserEntity getUserEntity(String username) {
        Optional<UserEntity> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isEmpty()) throw new UserException(UserExceptionType.NOT_FOUND_USERNAME);
        return optionalUser.get();
    }
    @Override
    // username 으로 user 조회 후 entity 를 userDetails 로 변환 후 반환
    public UserDetails loadUserByUsername(String username) {
        Optional<UserEntity> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isEmpty()) throw new UserException(UserExceptionType.NOT_FOUND_USERNAME);
        UserEntity user = optionalUser.get();
        return CustomUserDetails.fromEntity(user);
    }
}
