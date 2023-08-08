package com.example.sns.article.repository;

import com.example.sns.article.entity.ArticleEntity;
import com.example.sns.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArticleRepository extends JpaRepository<ArticleEntity, Long> {
    List<ArticleEntity> findAllByUser(UserEntity User);
}
