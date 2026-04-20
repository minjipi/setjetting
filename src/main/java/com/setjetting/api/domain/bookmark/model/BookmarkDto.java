package com.setjetting.api.domain.bookmark.model;

import com.setjetting.api.domain.content.model.Content;
import com.setjetting.api.domain.place.model.Place;
import lombok.*;

public class BookmarkDto {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BookmarkPlaceRes {
        private Long placeIdx;
        private String name;
        private String address;
        private String placeImageUrl;
        private Long contentIdx;
        private String contentTitle;
        private String contentType;
        private String posterImageUrl;

        public static BookmarkPlaceRes of(Bookmark bookmark) {
            Place place = bookmark.getPlace();
            Content content = place.getContent();
            return BookmarkPlaceRes.builder()
                    .placeIdx(place.getIdx())
                    .name(place.getName())
                    .address(place.getAddress())
                    .placeImageUrl(place.getPlaceImageUrl())
                    .contentIdx(content.getIdx())
                    .contentTitle(content.getTitle())
                    .contentType(content.getContentType())
                    .posterImageUrl(content.getPosterImageUrl())
                    .build();
        }
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ToggleRes {
        private boolean bookmarked;
    }
}
