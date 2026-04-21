package com.setjetting.api.domain.post.model.dto;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SnsPostRes {
    private Long postIdx;
    private Long placeIdx;
    private String placeName;
    private String contentTitle;
    private String contentType;
    private String userName;
    private String title;
    private String description;
    private String mainImageUrl;
    private long createdAtMs;
    private int imageCount;
}
