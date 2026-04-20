package com.setjetting.api.domain.place.model.dto;

import com.setjetting.api.domain.content.model.Content;
import com.setjetting.api.domain.place.model.entity.Place;
import com.setjetting.api.domain.scene.Scene;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ContentDetailRes {
    private Long contentIdx;
    private String title;
    private String englishTitle;
    private String posterImageUrl;
    private List<PlaceDto> places;

    @Getter
    @Builder
    public static class PlaceDto {
        private Long placeIdx;
        private String name;
        private String address;
        private String placeImageUrl;
        private String description; // 장소 설명 추가
        private List<SceneDto> scenes; // 장면 목록 추가

        public static PlaceDto of(Place place) {
            return PlaceDto.builder()
                    .placeIdx(place.getIdx())
                    .name(place.getName())
                    .address(place.getAddress())
                    .placeImageUrl(place.getPlaceImageUrl())
                    .description(place.getDescription())
                    .scenes(place.getScenes().stream()
                            .map(SceneDto::of)
                            .collect(Collectors.toList()))
                    .build();
        }
    }

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

    public static ContentDetailRes of(Content content) {
        return ContentDetailRes.builder()
                .contentIdx(content.getIdx())
                .title(content.getTitle())
                .englishTitle(content.getEnglishTitle())
                .posterImageUrl(content.getPosterImageUrl())
                .places(content.getPlaces().stream()
                        .map(PlaceDto::of)
                        .collect(Collectors.toList()))
                .build();
    }
}