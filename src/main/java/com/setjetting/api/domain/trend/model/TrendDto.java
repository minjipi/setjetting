package com.setjetting.api.domain.trend.model;

import lombok.*;

public class TrendDto {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PlaceTrendRes {
        private Long placeIdx;
        private String name;
        private String englishName;
        private String address;
        private Double latitude;
        private Double longitude;
        private String placeImageUrl;
        private String contentTitle;
        private Long contentIdx;
        private Double trendScore;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ContentTrendRes {
        private Long contentIdx;
        private String title;
        private String englishTitle;
        private String posterImageUrl;
        private String contentType;
        private Double trendScore;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecordActivityReq {
        private ActivityType type;
    }
}
