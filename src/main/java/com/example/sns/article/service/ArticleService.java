package com.example.sns.article.service;

import com.example.sns.article.dto.CreateArticleDto;
import com.example.sns.article.dto.ReadArticleDto;
import com.example.sns.article.dto.ReadArticleListDto;
import com.example.sns.article.entity.ArticleEntity;
import com.example.sns.article.entity.ArticleImageEntity;
import com.example.sns.article.repository.ArticleImageRepository;
import com.example.sns.article.repository.ArticleRepository;
import com.example.sns.global.dto.ResponseDto;
import com.example.sns.user.entity.UserEntity;
import com.example.sns.user.repository.UserRepository;
import com.example.sns.user.service.CustomUserDetailsManager;
import org.springframework.http.HttpStatus;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ArticleService {
    private final ArticleRepository articleRepository;
    private final ArticleImageRepository articleImageRepository;
    private final UserRepository userRepository;
    private final UserDetailsManager userDetailsManager;

    public ArticleService(ArticleRepository articleRepository, ArticleImageRepository articleImageRepository, UserRepository userRepository, UserDetailsManager userDetailsManager) {
        this.articleRepository = articleRepository;
        this.articleImageRepository = articleImageRepository;
        this.userRepository = userRepository;
        this.userDetailsManager = userDetailsManager;
    }

    public ResponseDto createArticle(String username, CreateArticleDto request, List<MultipartFile> articleImages) {
        // username 을 기준으로 user 조회
        UserEntity user = ((CustomUserDetailsManager) userDetailsManager).getUserEntity(username);

        // 사용자가 이미지를 첨부하지 않은 경우
        // articleImages 의 첫 인덱스의 값이 비었는지 확인하는 이유는
        // postman 에서 테스트 시 Body 에 key 값만 할당하고 업로드할 파일을 선택하지 않는 경우
        // List 가 빈 상태로 요청이 들어오는 것이 아닌 빈 MultipartFile 이 하나 포함되서 들어옴
        if (CollectionUtils.isEmpty(articleImages) || articleImages.get(0).isEmpty()) {
            // draft 를 true 로 하는 articleEntity 저장
            ArticleEntity articleEntity = ArticleEntity.builder()
                    .title(request.getTitle())
                    .content(request.getContent())
                    .draft(true)
                    .user(user)
                    .build();
            articleRepository.save(articleEntity);
            ResponseDto response = new ResponseDto();
            response.setHttpStatus(HttpStatus.OK);
            response.setMessage("피드 등록이 완료되었습니다.");
            return response;
        }
        // 이미지가 첨부된 경우
        // draft 를 false 로 하는 articleEntity 저장
        ArticleEntity articleEntity = ArticleEntity.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .draft(false)
                .user(user)
                .build();
        articleRepository.save(articleEntity);

        // 이미지 저장 디렉토리 생성
        String imageDir = String.format("images/article/%s/", articleEntity.getId());
        try {
            Files.createDirectories(Path.of(imageDir));
        } catch (IOException ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "디렉토리 생성에 실패");
        }

        LocalDateTime createTime = LocalDateTime.now();
        int imageNo = 0;
        // 이미지 저장
        List<ArticleImageEntity> articleImageEntities = new ArrayList<>();
        for (MultipartFile image : articleImages) {
            String originalFileName = image.getOriginalFilename();
            String[] fileNameSplit = originalFileName.split("\\.");
            String extension = fileNameSplit[fileNameSplit.length - 1];
            String articleImageFileName = String.format("%s_imageNo_%s.%s", createTime.toString(), imageNo++, extension);
            String articleImagePath = imageDir + articleImageFileName;

            try {
                image.transferTo(Path.of(articleImagePath));
            } catch (IOException ex) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "이미지 업로드 실패");
            }

            ArticleImageEntity articleImageEntity = ArticleImageEntity.builder()
                    .articleImage(String.format("/article/image/%s/%s", articleEntity.getId(), articleImageFileName))
                    .article(articleEntity)
                    .build();
            System.out.println(articleImageEntity.getArticleImage());
            articleImageEntities.add(articleImageEntity);
        }
        articleImageRepository.saveAll(articleImageEntities);

        ResponseDto response = new ResponseDto();
        response.setHttpStatus(HttpStatus.OK);
        response.setMessage("피드 등록이 완료되었습니다.");
        return response;
    }

    // target user 의 모든 피드 조회
    public List<ReadArticleListDto> readAllArticle(String targetUser) {
        // 사용자가 조회할 사용자를 입력하지 않은 경우
        if (targetUser.isEmpty() || targetUser.isBlank()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "조회할 사용자를 입력해주세요");
        UserEntity targetUserEntity = ((CustomUserDetailsManager) userDetailsManager).getUserEntity(targetUser);

        List<ArticleEntity> articleEntities = articleRepository.findAllByUser(targetUserEntity);
        if (articleEntities.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 사용자가 작성한 글이 없습니다.");

        List<ReadArticleListDto> readArticleDtoList = new ArrayList<>();
        for (ArticleEntity article : articleEntities) {
            if (article.getDraft()) {
                // 기본 이미지 반환
                String basicImage = "/article/image/basic/basicImage.png";
                readArticleDtoList.add(
                        ReadArticleListDto.builder()
                                .username(targetUser)
                                .title(article.getTitle())
                                .content(article.getContent())
                                .articleImage(basicImage)
                                .build()
                );
            } else {
                String articleImage = articleImageRepository.findFirstByArticle(article).getArticleImage();
                readArticleDtoList.add(
                        ReadArticleListDto.builder()
                                .username(targetUser)
                                .title(article.getTitle())
                                .content(article.getContent())
                                .articleImage(articleImage)
                                .build()
                );
            }
        }
        return readArticleDtoList;
    }

    public ReadArticleDto readArticle(Long articleId) {
        Optional<ArticleEntity> optionalArticleEntity = articleRepository.findById(articleId);
        if (optionalArticleEntity.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 피드가 존재하지 않습니다.");

        ArticleEntity articleEntity = optionalArticleEntity.get();
        String basicImage = "/article/image/basic/basicImage.png";
        List<String> articleImageList = new ArrayList<>();

        if (articleEntity.getDraft()) {
            articleImageList.add(basicImage);
            return ReadArticleDto.builder()
                    .username(articleEntity.getUser().getUsername())
                    .title(articleEntity.getTitle())
                    .content(articleEntity.getContent())
                    .articleImages(articleImageList)
                    .build();
        }

        List<ArticleImageEntity> articleImageEntityList = articleImageRepository.findAllByArticle(articleEntity);
        for (ArticleImageEntity articleImageEntity : articleImageEntityList) {
            articleImageList.add(articleImageEntity.getArticleImage());
        }
        return ReadArticleDto.builder()
                .username(articleEntity.getUser().getUsername())
                .title(articleEntity.getTitle())
                .content(articleEntity.getContent())
                .articleImages(articleImageList)
                .build();
    }
}
