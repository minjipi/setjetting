package com.setjetting.api.domain.place.model.dto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class CreatePlaceReq {

    @NotNull(message = "작품 ID는 필수입니다.")
    @Schema(description = "연결할 작품(Content)의 식별값", example = "1")
    private Long contentIdx;

    @NotBlank(message = "장소명은 필수입니다.")
    @Schema(description = "장소명", example = "가마쿠라 코코마에역 건널목")
    private String name;

    @Schema(description = "영문 장소명", example = "Kamakurakokomae Station Railway Crossing")
    private String englishName;

    @NotBlank(message = "주소는 필수입니다.")
    @Schema(description = "주소", example = "1 Chome-1-1 Shichirigahama, Kamakura, Kanagawa")
    private String address;

    @NotNull(message = "위도는 필수입니다.")
    @Schema(description = "위도", example = "35.3067")
    private Double latitude;

    @NotNull(message = "경도는 필수입니다.")
    @Schema(description = "경도", example = "139.5022")
    private Double longitude;

    @Schema(description = "장소 사진 URL", example = "https://example.com/place.jpg")
    private String placeImageUrl;

    @Schema(description = "장소 설명", example = "강백호가 소연이에게 인사하던 바로 그 건널목입니다.")
    private String description;
}