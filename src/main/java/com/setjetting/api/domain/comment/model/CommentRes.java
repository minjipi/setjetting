package com.setjetting.api.domain.comment.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentRes {
    private Long commentIdx;
    private String writerName;
    private String content;
    private long createdAtMs;
}