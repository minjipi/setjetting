package com.setjetting.api.domain.place.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatePlaceRes {
    @Schema(description = "생성된 장소의 식별값", example = "1")
    private Long placeIdx;

    @Schema(description = "메시지", example = "장소 등록이 완료되었습니다.")
    private String message;

    public static CreatePlaceRes of(Long placeIdx) {
        return CreatePlaceRes.builder()
                .placeIdx(placeIdx)
                .message("장소 등록이 완료되었습니다.")
                .build();
    }
}