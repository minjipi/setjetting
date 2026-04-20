package com.setjetting.api.domain.post;

import com.setjetting.api.common.model.BaseResponse;
import com.setjetting.api.domain.post.model.dto.CreatePostReq;
import com.setjetting.api.domain.post.model.dto.CreatePostRes;
import com.setjetting.api.domain.post.model.dto.PostDetailRes;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/post")
@Tag(name = "포스팅 컨트롤러")
public class PostController {

    private final PostService postService;

    @PostMapping("/write")
    @Operation(summary = "새 포스팅 등록", description = "이미지와 텍스트가 포함된 새로운 포스팅을 등록합니다.")
    public ResponseEntity<BaseResponse<CreatePostRes>> createPost(@RequestBody CreatePostReq request) {
        CreatePostRes response = postService.createPost(request);
        return ResponseEntity.ok(BaseResponse.success(response));
    }

    @GetMapping("/{idx}")
    @Operation(summary = "포스팅 상세 조회", description = "특정 포스팅의 제목, 내용, 이미지 목록을 조회합니다.")
    public ResponseEntity<BaseResponse<PostDetailRes>> getPostDetail(@PathVariable Long idx) {
        PostDetailRes response = postService.getPostDetail(idx);
        return ResponseEntity.ok(BaseResponse.success(response));
    }


}