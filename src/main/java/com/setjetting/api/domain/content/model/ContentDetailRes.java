package com.setjetting.api.domain.content.model;

import com.setjetting.api.domain.place.model.PlaceListRes;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "작품 상세 페이지 응답")
public class ContentDetailRes {
    @Schema(description = "작품 IDX", example = "1")
    private Long idx;

    @Schema(description = "작품 제목", example = "너의 이름은.")
    private String title;

    @Schema(description = "작품 영문 제목", example = "Your Name")
    private String englishTitle;

    @Schema(description = "포스터 이미지", example = "https://example.com/poster.jpg")
    private String posterImageUrl;

    @Schema(description = "작품 설명", example = "도쿄 소년과 시골 소녀의 이야기...")
    private String description;

    @Schema(description = "등록된 성지(장소) 총 개수", example = "5")
    private int placeCount;

    @Schema(description = "연관된 장소 목록")
    private List<PlaceListRes> places;

    public static ContentDetailRes of(Content content) {
        return ContentDetailRes.builder()
                .idx(content.getIdx())
                .title(content.getTitle())
                .englishTitle(content.getEnglishTitle())
                .posterImageUrl(content.getPosterImageUrl())
                .description(content.getDescription())
                .placeCount(content.getPlaces().size()) // 연관관계 리스트 크기 활용
                .places(content.getPlaces().stream()
                        .map(PlaceListRes::of)
                        .collect(Collectors.toList()))
                .build();
    }
}