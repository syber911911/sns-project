package com.example.sns.article.service;

import com.example.sns.article.dto.CreateArticleDto;
import com.example.sns.article.dto.ReadArticleDto;
import com.example.sns.article.dto.ReadArticleListDto;
import com.example.sns.article.dto.UpdateArticleDto;
import com.example.sns.article.entity.ArticleEntity;
import com.example.sns.article.entity.ArticleImageEntity;
import com.example.sns.article.repository.ArticleImageRepository;
import com.example.sns.article.repository.ArticleRepository;
import com.example.sns.global.dto.ResponseDto;
import com.example.sns.user.entity.UserEntity;
import com.example.sns.user.exception.UserException;
import com.example.sns.user.exception.UserExceptionType;
import com.example.sns.user.repository.UserRepository;
import com.example.sns.user.service.CustomUserDetailsManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
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

//    public ResponseDto createArticle(String username, CreateArticleDto request, List<MultipartFile> articleImages) {
//        // username 을 기준으로 user 조회
//        UserEntity user = ((CustomUserDetailsManager) userDetailsManager).getUserEntity(username);
//
//        // 사용자가 이미지를 첨부하지 않은 경우
//        // articleImages 의 첫 인덱스의 값이 비었는지 확인하는 이유는
//        // postman 에서 테스트 시 Body 에 key 값만 할당하고 업로드할 파일을 선택하지 않는 경우
//        // List 가 빈 상태로 요청이 들어오는 것이 아닌 빈 MultipartFile 이 하나 포함되서 들어옴
//        if (CollectionUtils.isEmpty(articleImages) || articleImages.get(0).isEmpty()) {
//            // draft 를 true 로 하는 articleEntity 저장
//            ArticleEntity articleEntity = ArticleEntity.builder()
//                    .title(request.getTitle())
//                    .content(request.getContent())
//                    .draft(true)
//                    .user(user)
//                    .build();
//            articleRepository.save(articleEntity);
//            ResponseDto response = new ResponseDto();
//            response.setHttpStatus(HttpStatus.OK);
//            response.setMessage("피드 등록이 완료되었습니다.");
//            return response;
//        }
//        // 이미지가 첨부된 경우
//        // draft 를 false 로 하는 articleEntity 저장
//        ArticleEntity articleEntity = ArticleEntity.builder()
//                .title(request.getTitle())
//                .content(request.getContent())
//                .draft(false)
//                .user(user)
//                .build();
//        articleRepository.save(articleEntity);
//
//        // 이미지 저장 디렉토리 생성
//        String imageDir = String.format("images/article/%s/", articleEntity.getId());
//        try {
//            Files.createDirectories(Path.of(imageDir));
//        } catch (IOException ex) {
//            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "디렉토리 생성에 실패");
//        }
//
//        LocalDateTime createTime = LocalDateTime.now();
//        int imageNo = 0;
//        // 이미지 저장
//        List<ArticleImageEntity> articleImageEntities = new ArrayList<>();
//        for (MultipartFile image : articleImages) {
//            String originalFileName = image.getOriginalFilename();
//            String[] fileNameSplit = originalFileName.split("\\.");
//            String extension = fileNameSplit[fileNameSplit.length - 1];
//            String articleImageFileName = String.format("%s_imageNo_%s.%s", createTime.toString(), imageNo++, extension);
//            String articleImagePath = imageDir + articleImageFileName;
//
//            try {
//                image.transferTo(Path.of(articleImagePath));
//            } catch (IOException ex) {
//                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "이미지 업로드 실패");
//            }
//
//            ArticleImageEntity articleImageEntity = ArticleImageEntity.builder()
//                    .articleImage(String.format("/article/image/%s/%s", articleEntity.getId(), articleImageFileName))
//                    .article(articleEntity)
//                    .build();
//            System.out.println(articleImageEntity.getArticleImage());
//            articleImageEntities.add(articleImageEntity);
//        }
//        articleImageRepository.saveAll(articleImageEntities);
//
//        ResponseDto response = new ResponseDto();
//        response.setHttpStatus(HttpStatus.OK);
//        response.setMessage("피드 등록이 완료되었습니다.");
//        return response;
//    }

    public ResponseDto createArticle(String username, CreateArticleDto request) {
        // username 을 기준으로 user 조회
        UserEntity user = ((CustomUserDetailsManager) userDetailsManager).getUserEntity(username);

        // 사용자가 이미지를 첨부하지 않은 경우
        // articleImages 의 첫 인덱스의 값이 비었는지 확인하는 이유는
        // postman 에서 테스트 시 Body 에 key 값만 할당하고 업로드할 파일을 선택하지 않는 경우
        // List 가 빈 상태로 요청이 들어오는 것이 아닌 빈 MultipartFile 이 하나 포함되서 들어옴
        if (CollectionUtils.isEmpty(request.getImages()) || request.getImages().get(0).isEmpty()) {
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

        this.saveImage(request.getImages(), articleEntity);

        ResponseDto response = new ResponseDto();
        response.setHttpStatus(HttpStatus.OK);
        response.setMessage("피드 등록이 완료되었습니다.");
        return response;
    }

    // target user 의 모든 피드 조회
    public List<ReadArticleListDto> readAllArticle(String targetUser) {
        // 사용자가 조회할 사용자를 입력하지 않은 경우
        if (targetUser.isEmpty() || targetUser.isBlank())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "조회할 사용자를 입력해주세요");
        UserEntity targetUserEntity = ((CustomUserDetailsManager) userDetailsManager).getUserEntity(targetUser);

        List<ArticleEntity> articleEntities = articleRepository.findAllByUserAndDeletedAtIsNull(targetUserEntity);
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
        Optional<ArticleEntity> optionalArticleEntity = articleRepository.findByIdAndDeletedAtIsNull(articleId);
        if (optionalArticleEntity.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 피드가 존재하지 않습니다.");

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

    public ResponseDto updateArticle(Long articleId, UpdateArticleDto request, String username) {
        Optional<ArticleEntity> optionalArticleEntity = articleRepository.findByIdAndDeletedAtIsNull(articleId);
        // 피드가 존재하지 않는 경우
        if (optionalArticleEntity.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 피드가 존재하지 않습니다.");
        ArticleEntity articleEntity = optionalArticleEntity.get();

        // 해당 피드 작성자의 요청이 아닌 경우
        if (!articleEntity.getUser().getUsername().equals(username))
            throw new UserException(UserExceptionType.WRONG_USER);

        // 어떠한 피드의 수정사항도 없는 경우
        if (request.isDtoEntireVariableNull())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "피드의 수정사항이 없습니다.");

        if (!request.titleIsNull()) {
            articleEntity.updateTitle(request.getTitle());
        }

        if (!request.contentIsNull()) {
            articleEntity.updateContent(request.getContent());
        }

        if (!request.addImagesIsNull()) {
            articleEntity.updateDraft(false);
            this.saveImage(request.getAddImages(), articleEntity);
        }

        if (!request.deleteImagesIsNull()) {
            this.deleteImage(request.getDeleteImages(), articleEntity);
            if (!articleImageRepository.existsByArticle(articleEntity)) {
                articleEntity.updateDraft(true);
            }
        }

        articleRepository.save(articleEntity);
        ResponseDto response = new ResponseDto();
        response.setMessage("피드 수정이 완료되었습니다.");
        response.setHttpStatus(HttpStatus.OK);
        return response;
    }

    public ResponseDto deleteArticle(Long articleId, String username) {
        LocalDateTime dateTime = LocalDateTime.now();

        Optional<ArticleEntity> optionalArticleEntity = articleRepository.findByIdAndDeletedAtIsNull(articleId);
        if (optionalArticleEntity.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "피드가 존재하지 않습니다.");
        ArticleEntity articleEntity = optionalArticleEntity.get();

        if (!articleEntity.getUser().getUsername().equals(username))
            throw new UserException(UserExceptionType.WRONG_USER);
        articleEntity.deleteArticle(dateTime);

        List<ArticleImageEntity> articleImageEntities = articleImageRepository.findAllByArticle(articleEntity);
        if (!CollectionUtils.isEmpty(articleImageEntities)) {
            this.deleteDir(articleEntity);
            articleImageRepository.deleteAll(articleImageEntities);
        } else log.info("{} 해당 피드는 삭제할 이미지가 존재하지 않습니다.", articleId);
        this.deleteDir(articleEntity); // 중간에 피드 수정으로 이미지가 모두 지워진 경우에 디렉토리는 남아있을 가능성이 있음

        articleEntity.deleteArticle(dateTime);
        articleRepository.save(articleEntity);

        ResponseDto response = new ResponseDto();
        response.setMessage("피드 삭제가 완료되었습니다.");
        response.setHttpStatus(HttpStatus.OK);
        return response;
    }

    public void saveImage(List<MultipartFile> images, ArticleEntity articleEntity) {
        // 이미지 저장 디렉토리 생성
        String imageDir = String.format("images/article/%s/", articleEntity.getId());
        try {
            Files.createDirectories(Path.of(imageDir));
        } catch (IOException ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "디렉토리 생성에 실패");
        }
        // 이미지 저장
        List<ArticleImageEntity> articleImageEntities = new ArrayList<>();

        for (MultipartFile image : images) {
            LocalDateTime createTime = LocalDateTime.now();
            String originalFileName = image.getOriginalFilename();
            String[] fileNameSplit = originalFileName.split("\\.");
            String extension = fileNameSplit[fileNameSplit.length - 1];
            String articleImageFileName = String.format("%s_%s.%s", createTime.toString(), articleEntity.getId(), extension);
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
    }

    public void deleteImage(List<String> images, ArticleEntity articleEntity) {
        List<ArticleImageEntity> articleImageEntities = new ArrayList<>();
        for (String image : images) {
            Optional<ArticleImageEntity> optionalArticleImageEntity = articleImageRepository.findByArticleImage(image);
            if (optionalArticleImageEntity.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            ArticleImageEntity articleImageEntity = optionalArticleImageEntity.get();

            if (!articleImageEntity.getArticle().equals(articleEntity))
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "해당 피드의 이미지가 아닙니다.");

            String[] splitFilePath = image.split("/");
            String filename = splitFilePath[splitFilePath.length - 1];
            String targetPath = String.format("images/article/%s/%s", articleEntity.getId(), filename);
            System.out.println(targetPath);
            File file = new File(targetPath);
            if (!file.delete()) throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "파일 삭제에 실패했습니다");
            articleImageEntities.add(articleImageEntity);
        }
        articleImageRepository.deleteAll(articleImageEntities);
    }

    public void deleteDir(ArticleEntity articleEntity) {
        String imageDir = String.format("images/article/%s", articleEntity.getId());
        File targetDir = new File(imageDir);
        if (targetDir.exists()) { // 해당 경로에 파일 or 디렉토리가 존재하는지
            File[] images = targetDir.listFiles();
            assert images != null; // 디렉토리는 존재하지만 파일은 없는 경우도 존재 가능
            for (File image : images) {
                if (!image.delete()) log.warn("{} 파일의 삭제가 정상적으로 처리되지 않았습니다.", image.getPath());
            }
        } else log.info("{} 해당 경로에 디렉토리 혹은 파일이 존재하지 않습니다.", targetDir.getPath());
        if (!targetDir.delete()) log.warn("{} 디렉토리의 삭제가 정상적으로 처리되지 않았습니다.", targetDir.getPath());
    }
}
