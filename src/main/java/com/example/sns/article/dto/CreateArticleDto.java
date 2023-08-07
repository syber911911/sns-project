package com.example.sns.article.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CreateArticleDto {
    @NotBlank(message = "피드의 제목을 입력해주세요.")
    private String title;
    @NotBlank(message = "피드의 내용을 입력해주세요.")
    private String content;
}
