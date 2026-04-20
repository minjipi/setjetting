package com.setjetting.api.domain.post.model.dto;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class CreatePostRes {
    private Long postIdx;

    public static CreatePostRes of(Long postIdx) {
        return CreatePostRes.builder()
                .postIdx(postIdx)
                .build();
    }
}