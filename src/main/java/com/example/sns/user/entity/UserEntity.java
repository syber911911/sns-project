package com.example.sns.user.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
// 빈 생성자로 무분별한 entity 생성을 막기 위함
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String username;
    @Column(nullable = false, unique = true)
    private String password;
    private String realName;
    @Column(unique = true)
    private String email;

    private String phone;
    private String address;
    private String profileImg;

    @Builder
    public UserEntity(Long id, String username, String password, String realName, String email, String phone, String address, String profileImg) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.realName = realName;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.profileImg = profileImg;
    }

    public void updateProfileImage(String imagePath) {
        this.profileImg = imagePath;
    }
}
