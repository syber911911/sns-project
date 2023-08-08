package com.example.sns.article.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@ToString
public class CreateArticleDto {
    @NotBlank(message = "피드의 제목을 입력해주세요.")
    private String title;
    @NotBlank(message = "피드의 내용을 입력해주세요.")
    private String content;
    @Nullable
    private List<MultipartFile> images;
}
