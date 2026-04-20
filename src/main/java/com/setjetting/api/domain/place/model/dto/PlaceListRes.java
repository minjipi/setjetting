package com.setjetting.api.domain.place.model.dto;

import com.setjetting.api.domain.place.model.entity.Place;
import com.setjetting.api.domain.scene.Scene;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlaceListRes {
    private Long placeIdx;
    private String name;
    private String englishName;
    private String address;
    private Double latitude;
    private Double longitude;
    private String placeImageUrl;

    @Schema(description = "장소 설명")
    private String description;

    @Schema(description = "관련 장면 목록")
    private List<SceneDto> scenes; // 엔티티 대신 DTO 사용

    @Getter
    @Builder
    public static class SceneDto {
        private String sceneImageUrl;
        private Integer episodeNumber;
        private Integer appearTime;
        private String sceneDescription;

        public static SceneDto of(Scene scene) {
            return SceneDto.builder()
                    .sceneImageUrl(scene.getSceneImageUrl())
                    .episodeNumber(scene.getEpisodeNumber())
                    .appearTime(scene.getAppearTime())
                    .sceneDescription(scene.getSceneDescription())
                    .build();
        }
    }

    public static PlaceListRes of(Place place) {
        return PlaceListRes.builder()
                .placeIdx(place.getIdx())
                .name(place.getName())
                .englishName(place.getEnglishName())
                .address(place.getAddress())
                .latitude(place.getLatitude())
                .longitude(place.getLongitude())
                .placeImageUrl(place.getPlaceImageUrl())
                .description(place.getDescription()) // 설명 추가
                .scenes(place.getScenes().stream()
                        .map(SceneDto::of)
                        .collect(Collectors.toList())) // 장면 목록 변환 및 추가
                .build();
    }
}