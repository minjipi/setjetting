package com.setjetting.api.domain.post.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "포스팅(이미지+글) 업로드 요청 DTO")
public class CreatePostReq {

    @Schema(description = "작성자(유저) IDX", example = "1")
    private Long userIdx;

    @Schema(description = "작성자 닉네임", example = "영화광")
    private String userNickname;

    @Schema(description = "포스팅 제목", example = "제주도 성지순례 다녀왔습니다")
    private String title;

    @Schema(description = "포스팅 전체 설명", example = "이번 주말에 촬영지에 다녀왔는데 너무 좋았어요.")
    private String description;

    @Schema(description = "연관 장소 IDX", example = "3")
    private Long placeIdx;

    @Schema(description = "업로드할 이미지 목록")
    private List<ImageReq> images;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ImageReq {
        @Schema(description = "이미지 URL", example = "https://s3.../image1.jpg")
        private String imageUrl;

        @Schema(description = "이미지 개별 설명", example = "여기가 주인공이 밥 먹던 식당입니다.")
        private String description;
    }
}