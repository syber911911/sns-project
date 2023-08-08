package com.example.sns.article.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
public class ReadArticleListDto {
    private String username;
    private String title;
    private String content;
    private String articleImage;
}
