package com.setjetting.api.domain.comment.model;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class CreateCommentRes {
    private Long commentIdx;

    public static CreateCommentRes of(Long commentIdx) {
        return CreateCommentRes.builder()
                .commentIdx(commentIdx)
                .build();
    }
}