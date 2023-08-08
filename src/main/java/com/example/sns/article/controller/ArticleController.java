package com.example.sns.article.controller;

import com.example.sns.article.dto.CreateArticleDto;
import com.example.sns.article.dto.ReadArticleDto;
import com.example.sns.article.dto.ReadArticleListDto;
import com.example.sns.article.dto.UpdateArticleDto;
import com.example.sns.article.service.ArticleService;
import com.example.sns.global.dto.ResponseDto;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/article")
public class ArticleController {
    private final ArticleService articleService;

    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    // 요청 Body 에 아무것도 포함하지 않으면 content type 이 비었다고 예외를 발생시킨다.
    // 해당 post 요청은 헤더의 content type 과 지정한 MediaType 과 일치할 때 요청이 처리된다.
    // multipart form data 혹은 application json 을 지정해 뒀으니 content type 이 빈 경우에 예외를 발생
    // 사실 해당 요청에서 둘다 비어있는 것은 말이 안된다.
    // 최초 피드 등록시 등록 이미지를 포함하지 않는 경우가 있기 때문에 required false
    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    // @RequestPart(value = "article") @Valid CreateArticleDto request, @RequestPart(value = "image", required = false) List<MultipartFile> articleImages
    public ResponseDto createArticle(@AuthenticationPrincipal String username, @ModelAttribute @Valid CreateArticleDto request) {
        return articleService.createArticle(username, request);
    }

    @GetMapping
    public List<ReadArticleListDto> readAllArticles(@RequestParam(value = "targetUser") String targetUser) {
        return articleService.readAllArticle(targetUser);
    }

    @GetMapping("/{articleId}")
    public ReadArticleDto readArticle(@PathVariable("articleId") Long articleId) {
        return articleService.readArticle(articleId);
    }

    @PutMapping(value = "/{articleId}", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseDto updateArticle(@PathVariable("articleId") Long articleId, @ModelAttribute UpdateArticleDto request, @AuthenticationPrincipal String username) {
        return articleService.updateArticle(articleId, request, username);
    }

    @DeleteMapping("/{articleId}")
    public ResponseDto deleteArticle(@PathVariable("articleId") Long articleId, @AuthenticationPrincipal String username) {
        return articleService.deleteArticle(articleId, username);
    }
}
