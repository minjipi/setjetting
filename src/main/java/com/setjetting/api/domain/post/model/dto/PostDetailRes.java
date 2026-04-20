package com.setjetting.api.domain.post.model.dto;
import com.setjetting.api.domain.post.model.entity.Post;
import lombok.*;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class PostDetailRes {
    private Long postIdx;
    private String title;
    private String description;
    private String userNickname;
    private List<ImageDto> images;

    @Getter
    @Builder
    public static class ImageDto {
        private String imageUrl;
        private String description;
    }

    public static PostDetailRes of(Post post) {
        return PostDetailRes.builder()
                .postIdx(post.getIdx())
                .title(post.getTitle())
                .description(post.getDescription())
                .userNickname(post.getUser().getName()) // 작성자 이름 맵핑
                .images(post.getImages().stream()
                        .map(img -> ImageDto.builder()
                                .imageUrl(img.getImageUrl())
                                .description(img.getDescription())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }
}