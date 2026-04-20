package com.setjetting.api.domain.post;

import com.setjetting.api.domain.post.model.dto.CreatePostReq;
import com.setjetting.api.domain.post.model.dto.CreatePostRes;
import com.setjetting.api.domain.post.model.dto.PostDetailRes;
import com.setjetting.api.domain.post.model.entity.Post;
import com.setjetting.api.domain.post.model.entity.PostImage;
import com.setjetting.api.domain.user.model.entity.User;
import com.setjetting.api.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Transactional
    public CreatePostRes createPost(CreatePostReq dto) {
        User user = userRepository.findById(dto.getUserIdx())
                .orElseThrow(() -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다."));

        Post post = Post.builder()
                .user(user)
                .title(dto.getTitle())
                .description(dto.getDescription())
                .build();

        if (dto.getImages() != null && !dto.getImages().isEmpty()) {
            for (CreatePostReq.ImageReq imageReq : dto.getImages()) {
                PostImage postImage = PostImage.builder()
                        .imageUrl(imageReq.getImageUrl())
                        .description(imageReq.getDescription())
                        .build();

                post.addImage(postImage);
            }
        }

        Long savedIdx = postRepository.save(post).getIdx();

        return CreatePostRes.of(savedIdx);
    }

    public PostDetailRes getPostDetail(Long idx) {
        Post post = postRepository.findByIdWithUserAndImages(idx)
                .orElseThrow(() -> new IllegalArgumentException("해당 포스팅을 찾을 수 없습니다."));

        return PostDetailRes.of(post);
    }


}