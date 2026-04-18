package com.setjetting.api.domain.contents;

import com.setjetting.api.common.model.BaseResponse;
import com.setjetting.api.domain.contents.model.ContentListRes;
import com.setjetting.api.domain.contents.model.CreateContentReq;
import com.setjetting.api.domain.contents.model.CreateContentRes;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/contents")
@Tag(name = "콘텐츠(작품) 컨트롤러")
public class ContentController {

    private final ContentService contentService;

    @PostMapping("/write")
    @Operation(summary = "새 작품 등록", description = "영화나 드라마 정보를 새롭게 등록합니다.")
    public ResponseEntity<BaseResponse<CreateContentRes>> createContent(@RequestBody CreateContentReq request) {
        CreateContentRes response = contentService.createContent(request);
        return ResponseEntity.ok(BaseResponse.success(response));
    }


    @GetMapping("/all")
    @Operation(summary = "메인 화면 작품 목록 조회", description = "전체 또는 타입별 드라마/영화 목록을 반환합니다.")
    public ResponseEntity<BaseResponse<List<ContentListRes>>> getContents(
            @RequestParam(required = false) String type) {
        List<ContentListRes> response = contentService.getContentList(type);
        return ResponseEntity.ok(BaseResponse.success(response));
    }
}