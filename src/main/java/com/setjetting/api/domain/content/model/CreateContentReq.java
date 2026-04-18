package com.setjetting.api.domain.content.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class CreateContentReq {
    @Schema(description = "작품 제목", example = "이태원 클라쓰")
    private String title;

    @Schema(description = "영문 제목", example = "Itaewon Class")
    private String englishTitle;

    @Schema(description = "콘텐츠 타입", example = "DRAMA")
    private String contentType;

    @Schema(description = "포스터 이미지 URL", example = "https://example.com/poster.jpg")
    private String posterImageUrl;

    @Schema(description = "작품 설명", example = "서울 이태원을 배경으로 한 청춘들의 이야기")
    private String description;

    public Content toEntity() {
        return Content.builder()
                .title(title)
                .englishTitle(englishTitle)
                .contentType(contentType)
                .posterImageUrl(posterImageUrl)
                .description(description)
                .build();
    }
}