package com.example.sns.article.repository;

import com.example.sns.article.entity.ArticleEntity;
import com.example.sns.article.entity.ArticleImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ArticleImageRepository extends JpaRepository<ArticleImageEntity, Long> {
    ArticleImageEntity findFirstByArticle(ArticleEntity article);
    List<ArticleImageEntity> findAllByArticle(ArticleEntity article);
    Optional<ArticleImageEntity> findByArticleImage(String articleImage);
    Boolean existsByArticle(ArticleEntity article);
}
