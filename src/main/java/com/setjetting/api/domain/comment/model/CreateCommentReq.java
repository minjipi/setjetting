package com.setjetting.api.domain.comment.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateCommentReq {
    @NotBlank(message = "댓글 내용은 공백일 수 없습니다.")
    private String content;
}