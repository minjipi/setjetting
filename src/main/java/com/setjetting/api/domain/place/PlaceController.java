package com.setjetting.api.domain.place;

import com.setjetting.api.common.model.BaseResponse;
import com.setjetting.api.domain.content.ContentService;
import com.setjetting.api.domain.content.model.ContentDetailRes;
import com.setjetting.api.domain.place.model.dto.CreatePlaceReq;
import com.setjetting.api.domain.place.model.dto.CreatePlaceRes;
import com.setjetting.api.domain.place.model.dto.PlaceListRes;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/place")
@Tag(name = "장소(촬영지) 컨트롤러")
public class PlaceController {

    private final PlaceService placeService;
    private final ContentService contentService;

    @PostMapping("/write")
    @Operation(summary = "새 장소 등록", description = "특정 작품에 속하는 새로운 촬영 장소를 등록합니다.")
    public ResponseEntity<BaseResponse<CreatePlaceRes>> createPlace(
            @Valid @RequestBody CreatePlaceReq request
    ) {
        CreatePlaceRes response = placeService.createPlace(request);
        return ResponseEntity.ok(BaseResponse.success(response));
    }

    @GetMapping("/all")
    @Operation(summary = "전체 장소 목록 조회", description = "시스템에 등록된 모든 촬영지 목록을 반환합니다.")
    public ResponseEntity<BaseResponse<List<PlaceListRes>>> getAllPlaces() {
        List<PlaceListRes> response = placeService.getAllPlaces();
        return ResponseEntity.ok(BaseResponse.success(response));
    }

    @GetMapping("/{placeIdx}")
    @Operation(summary = "장소 기준 작품 상세 조회", description = "장소 ID를 통해 해당 작품의 전체 상세 정보를 조회합니다.")
    public ResponseEntity<BaseResponse<ContentDetailRes>> getContentDetailByPlace(@PathVariable Long placeIdx) {
        return ResponseEntity.ok(BaseResponse.success(contentService.getContentDetailByPlace(placeIdx)));
    }

    @GetMapping("/content/{contentIdx}")
    @Operation(summary = "작품별 장소 목록 조회", description = "특정 작품(Content IDX)에 속한 모든 촬영 장소 목록을 반환합니다.")
    public ResponseEntity<BaseResponse<List<PlaceListRes>>> getPlacesByContent(
            @PathVariable Long contentIdx
    ) {
        List<PlaceListRes> response = placeService.getPlacesByContentIdx(contentIdx);
        return ResponseEntity.ok(BaseResponse.success(response));
    }
}