package com.example.sns.article.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "articleImages")
public class ArticleImageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String articleImage;

    @ManyToOne
    @JoinColumn(name = "article_id")
    private ArticleEntity article;

    @Builder
    private ArticleImageEntity(String articleImage, ArticleEntity article) {
        this.articleImage = articleImage;
        this.article = article;
    }
}
