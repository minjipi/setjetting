package com.setjetting.api.domain.post;

import com.setjetting.api.common.model.BaseResponse;
import com.setjetting.api.domain.comment.model.CreateCommentReq;
import com.setjetting.api.domain.comment.model.CreateCommentRes;
import com.setjetting.api.domain.post.model.dto.CreatePostReq;
import com.setjetting.api.domain.post.model.dto.CreatePostRes;
import com.setjetting.api.domain.post.model.dto.MyPostRes;
import com.setjetting.api.domain.post.model.dto.PostDetailRes;
import com.setjetting.api.domain.post.model.dto.SnsPostRes;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/post")
@Tag(name = "포스팅 컨트롤러")
public class PostController {

    private final PostService postService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "이미지 업로드", description = "이미지를 서버에 업로드하고 URL을 반환합니다.")
    public ResponseEntity<BaseResponse<String>> uploadImage(@RequestParam("file") MultipartFile file) {
        String url = postService.uploadImage(file);
        return ResponseEntity.ok(BaseResponse.success(url));
    }

    @PostMapping("/write")
    @Operation(summary = "새 포스팅 등록", description = "이미지와 텍스트가 포함된 새로운 포스팅을 등록합니다.")
    public ResponseEntity<BaseResponse<CreatePostRes>> createPost(@RequestBody CreatePostReq request) {
        CreatePostRes response = postService.createPost(request);
        return ResponseEntity.ok(BaseResponse.success(response));
    }

    @GetMapping("/feed")
    @Operation(summary = "SNS 피드 조회", description = "모든 유저의 포스팅을 최신순으로 반환합니다.")
    public ResponseEntity<BaseResponse<List<SnsPostRes>>> getFeed() {
        return ResponseEntity.ok(BaseResponse.success(postService.getFeed()));
    }

    @GetMapping("/my")
    @Operation(summary = "내 포스팅 목록 조회", description = "로그인한 유저의 포스팅 목록을 반환합니다.")
    public ResponseEntity<BaseResponse<List<MyPostRes>>> getMyPosts() {
        return ResponseEntity.ok(BaseResponse.success(postService.getMyPosts()));
    }

    @GetMapping("/user/{userIdx}")
    @Operation(summary = "특정 유저 포스팅 목록 조회", description = "특정 유저의 포스팅 목록을 최신순으로 반환합니다.")
    public ResponseEntity<BaseResponse<List<SnsPostRes>>> getPostsByUser(@PathVariable Long userIdx) {
        return ResponseEntity.ok(BaseResponse.success(postService.getPostsByUser(userIdx)));
    }

    @GetMapping("/{idx}")
    @Operation(summary = "포스팅 상세 조회", description = "특정 포스팅의 제목, 내용, 이미지 목록을 조회합니다.")
    public ResponseEntity<BaseResponse<PostDetailRes>> getPostDetail(@PathVariable Long idx) {
        PostDetailRes response = postService.getPostDetail(idx);
        return ResponseEntity.ok(BaseResponse.success(response));
    }

    @PostMapping("/{postIdx}/comment")
    @Operation(summary = "댓글 등록", description = "특정 포스팅에 댓글을 작성합니다.")
    public ResponseEntity<BaseResponse<CreateCommentRes>> createComment(
            @PathVariable Long postIdx,
            @Valid @RequestBody CreateCommentReq request) {

        CreateCommentRes response = postService.createComment(postIdx, request);
        return ResponseEntity.ok(BaseResponse.success(response));
    }

}