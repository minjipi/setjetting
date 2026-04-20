package com.setjetting.api.domain.place.model.dto;

import com.setjetting.api.domain.place.model.entity.Place;
import lombok.*;

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

    public static PlaceListRes of(Place place) {
        return PlaceListRes.builder()
                .placeIdx(place.getIdx())
                .name(place.getName())
                .englishName(place.getEnglishName())
                .address(place.getAddress())
                .latitude(place.getLatitude())
                .longitude(place.getLongitude())
                .placeImageUrl(place.getPlaceImageUrl())
                .build();
    }
}