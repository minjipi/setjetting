package com.setjetting.api.domain.contents.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "콘텐츠 등록 응답")
public class CreateContentRes {

    @Schema(description = "생성된 콘텐츠의 식별값(ID)", example = "1")
    private Long contentIdx;

    @Schema(description = "등록 처리 메시지", example = "콘텐츠 등록이 완료되었습니다.")
    private String message;

    public static CreateContentRes of(Long contentIdx) {
        return CreateContentRes.builder()
                .contentIdx(contentIdx)
                .message("콘텐츠 등록이 완료되었습니다.")
                .build();
    }
}