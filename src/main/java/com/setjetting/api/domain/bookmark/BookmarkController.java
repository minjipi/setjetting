package com.setjetting.api.domain.bookmark;

import com.setjetting.api.common.model.BaseResponse;
import com.setjetting.api.config.security.AuthUserDetails;
import com.setjetting.api.domain.bookmark.model.BookmarkDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/bookmark")
@Tag(name = "북마크 컨트롤러")
public class BookmarkController {

    private final BookmarkService bookmarkService;

    @PostMapping("/place/{placeIdx}")
    @Operation(summary = "성지 북마크 토글", description = "북마크 추가/제거를 토글합니다.")
    public ResponseEntity<BaseResponse<BookmarkDto.ToggleRes>> toggle(
            @PathVariable Long placeIdx,
            @AuthenticationPrincipal AuthUserDetails userDetails) {
        return ResponseEntity.ok(BaseResponse.success(
                bookmarkService.toggle(userDetails.getIdx(), placeIdx)));
    }

    @GetMapping("/places")
    @Operation(summary = "내 저장 성지 목록", description = "로그인한 사용자의 북마크 성지 목록을 반환합니다.")
    public ResponseEntity<BaseResponse<List<BookmarkDto.BookmarkPlaceRes>>> getMyPlaces(
            @AuthenticationPrincipal AuthUserDetails userDetails) {
        return ResponseEntity.ok(BaseResponse.success(
                bookmarkService.getBookmarkedPlaces(userDetails.getIdx())));
    }

    @GetMapping("/place/{placeIdx}/status")
    @Operation(summary = "북마크 상태 조회", description = "특정 성지의 북마크 여부를 반환합니다. 비로그인 시 false.")
    public ResponseEntity<BaseResponse<BookmarkDto.ToggleRes>> getStatus(
            @PathVariable Long placeIdx,
            @AuthenticationPrincipal AuthUserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.ok(BaseResponse.success(new BookmarkDto.ToggleRes(false)));
        }
        return ResponseEntity.ok(BaseResponse.success(
                new BookmarkDto.ToggleRes(bookmarkService.isBookmarked(userDetails.getIdx(), placeIdx))));
    }
}
