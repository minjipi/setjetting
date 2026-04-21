package com.setjetting.api.domain.post.model.dto;
import com.setjetting.api.domain.comment.Comment;
import com.setjetting.api.domain.comment.model.CommentRes;
import com.setjetting.api.domain.post.model.entity.Post;
import lombok.*;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class PostDetailRes {
    private Long postIdx;
    private Long userIdx;
    private String title;
    private String description;
    private String userNickname;
    private Long placeIdx;
    private String placeName;
    private String contentTitle;
    private String contentType;
    private long createdAtMs;
    private List<ImageDto> images;

    private List<CommentRes> comments;

    @Getter
    @Builder
    public static class ImageDto {
        private String imageUrl;
        private String description;
    }

    public static PostDetailRes of(Post post, String placeName, String contentTitle, String contentType) {
        return PostDetailRes.builder()
                .postIdx(post.getIdx())
                .userIdx(post.getUser().getIdx())
                .title(post.getTitle())
                .description(post.getDescription())
                .userNickname(post.getUser().getName())
                .placeIdx(post.getPlaceIdx())
                .placeName(placeName)
                .contentTitle(contentTitle)
                .contentType(contentType)
                .createdAtMs(post.getCreatedAt() != null ? post.getCreatedAt().getTime() : 0L)
                .images(post.getImages().stream()
                        .map(img -> ImageDto.builder()
                                .imageUrl(img.getImageUrl())
                                .description(img.getDescription())
                                .build())
                        .collect(Collectors.toList()))
                .comments(post.getComments().stream()
                        .sorted(Comparator.comparing(Comment::getCreatedAt).reversed()) // 댓글 최신순 정렬
                        .map(comment -> CommentRes.builder()
                                .commentIdx(comment.getIdx())
                                .writerName(comment.getUser() != null ? comment.getUser().getName() : "알 수 없음")
                                .content(comment.getContent())
                                .createdAtMs(comment.getCreatedAt() != null ? comment.getCreatedAt().getTime() : 0L)
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }
}
