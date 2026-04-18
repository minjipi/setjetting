package com.setjetting.api.domain.content.model;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ContentListRes {
    private Long contentIdx;
    private String title;
    private String englishTitle;
    private String posterImageUrl;

    public static ContentListRes of(Content content) {
        return ContentListRes.builder()
                .contentIdx(content.getIdx())
                .title(content.getTitle())
                .englishTitle(content.getEnglishTitle())
                .posterImageUrl(content.getPosterImageUrl())
                .build();
    }
}