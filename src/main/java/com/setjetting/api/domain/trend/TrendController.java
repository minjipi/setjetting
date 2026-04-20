package com.setjetting.api.domain.trend;

import com.setjetting.api.common.model.BaseResponse;
import com.setjetting.api.config.security.AuthUserDetails;
import com.setjetting.api.domain.trend.model.TrendDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/trend")
@RequiredArgsConstructor
public class TrendController {

    private final TrendService trendService;

    @GetMapping("/places/top5")
    public BaseResponse<List<TrendDto.PlaceTrendRes>> getTopPlaces() {
        return BaseResponse.success(trendService.getTopPlaces());
    }

    @GetMapping("/contents/top5")
    public BaseResponse<List<TrendDto.ContentTrendRes>> getTopContents() {
        return BaseResponse.success(trendService.getTopContents());
    }

    @PostMapping("/place/{placeId}/activity")
    public BaseResponse<Void> recordPlaceActivity(
            @PathVariable Long placeId,
            @RequestBody TrendDto.RecordActivityReq req,
            @AuthenticationPrincipal AuthUserDetails userDetails) {
        Long userId = userDetails != null ? userDetails.getIdx() : null;
        trendService.recordPlaceActivity(placeId, userId, req.getType());
        return BaseResponse.success(null);
    }

    @PostMapping("/content/{contentId}/activity")
    public BaseResponse<Void> recordContentActivity(
            @PathVariable Long contentId,
            @RequestBody TrendDto.RecordActivityReq req,
            @AuthenticationPrincipal AuthUserDetails userDetails) {
        Long userId = userDetails != null ? userDetails.getIdx() : null;
        trendService.recordContentActivity(contentId, userId, req.getType());
        return BaseResponse.success(null);
    }
}
