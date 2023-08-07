package com.example.sns.article.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Optional;

@Getter
@Setter
@ToString
public class UpdateArticleDto {
    private String title;
    private String content;
}
