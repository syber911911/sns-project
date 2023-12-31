package com.example.sns.article.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
@Builder
public class ReadArticleDto {
    private String username;
    private String title;
    private String content;
    private List<String> articleImages;
}
