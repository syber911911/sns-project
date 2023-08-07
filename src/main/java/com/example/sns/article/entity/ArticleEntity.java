package com.example.sns.article.entity;

import com.example.sns.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.parameters.P;

import java.time.LocalDateTime;

@Entity
@Getter
// 빈 생성자로 무분별한 entity 생성을 막기 위함
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "articles")
public class ArticleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String content;
    // draft 를 기준으로 기본 이미지를 보여줄지 등록된 이미지를 보여줄지 구분
    // draft 가 true 라면 등록 이미지가 없는 경우 (기본 이미지 반환)
    // false 라면 등록 이미지 반환
    private Boolean draft;
    private LocalDateTime deletedAt;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Builder
    public ArticleEntity(String title, String content, Boolean draft, UserEntity user) {
        this.title = title;
        this.content = content;
        this.draft = draft;
        this.user = user;
    }

    public void updateDraft(Boolean draft) {
        this.draft = draft;
    }

    public void updateArticle(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public void deleteArticle(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }
}
