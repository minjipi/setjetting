package com.setjetting.api.domain.scene;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class PlaceDetailRes {
    private Long placeIdx;
    private String name;
    private String address;
    private List<SceneDto> scenes;

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
}