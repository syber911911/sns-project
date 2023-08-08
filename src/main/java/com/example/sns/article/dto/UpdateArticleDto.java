package com.example.sns.article.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

@Getter
@Setter
@ToString
public class UpdateArticleDto {
    @Nullable
    private String title;
    @Nullable
    private String content;
    @Nullable
    private List<MultipartFile> addImages;
    @Nullable
    private List<String> deleteImages;

    public boolean titleIsNull() {
        return title == null || title.isEmpty() || title.isBlank();
    }

    public boolean contentIsNull() {
        return content == null || content.isEmpty() || content.isBlank();
    }

    public boolean addImagesIsNull() {
        return CollectionUtils.isEmpty(addImages) || addImages.get(0).isEmpty();
    }

    public boolean deleteImagesIsNull() {
        return CollectionUtils.isEmpty(deleteImages);
    }

    // 필드 중 하나라도 빈 상태가 아니라면 false
    public boolean isDtoEntireVariableNull() {
        return this.titleIsNull() && this.contentIsNull() && this.addImagesIsNull() && this.deleteImagesIsNull();
    }
}
