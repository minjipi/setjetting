package com.setjetting.api.domain.post.model.dto;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyPostRes {
    private Long postIdx;
    private Long placeIdx;
    private String placeName;
    private String contentTitle;
    private String contentType;
    private String mainImageUrl;
    private String createdAt;
    private int imageCount;
}
