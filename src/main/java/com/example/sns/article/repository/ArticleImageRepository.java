package com.example.sns.article.repository;

import com.example.sns.article.entity.ArticleImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleImageRepository extends JpaRepository<ArticleImageEntity, Long> {
}
