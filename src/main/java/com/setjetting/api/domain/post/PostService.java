package com.setjetting.api.domain.post;

import com.setjetting.api.domain.place.PlaceRepository;
import com.setjetting.api.domain.place.model.entity.Place;
import com.setjetting.api.domain.post.model.dto.CreatePostReq;
import com.setjetting.api.domain.post.model.dto.CreatePostRes;
import com.setjetting.api.domain.post.model.dto.MyPostRes;
import com.setjetting.api.domain.post.model.dto.PostDetailRes;
import com.setjetting.api.domain.post.model.dto.SnsPostRes;
import com.setjetting.api.domain.post.model.entity.Post;
import com.setjetting.api.domain.post.model.entity.PostImage;
import com.setjetting.api.domain.user.model.entity.User;
import com.setjetting.api.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import com.setjetting.api.config.security.AuthUserDetails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PlaceRepository placeRepository;

    @Value("${project.upload.path}")
    private String uploadPath;

    private Long getCurrentUserIdx() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof AuthUserDetails details)) {
            throw new IllegalStateException("인증 정보가 없습니다.");
        }
        return details.getIdx();
    }

    @Transactional
    public CreatePostRes createPost(CreatePostReq dto) {
        User user = userRepository.findById(getCurrentUserIdx())
                .orElseThrow(() -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다."));

        Post post = Post.builder()
                .user(user)
                .title(dto.getTitle())
                .description(dto.getDescription())
                .placeIdx(dto.getPlaceIdx())
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

    @Transactional
    public String uploadImage(MultipartFile file) {
        try {
            String ext = StringUtils.getFilenameExtension(file.getOriginalFilename());
            String filename = UUID.randomUUID() + (ext != null ? "." + ext : "");
            Path dir = Paths.get(uploadPath);
            if (!Files.exists(dir)) Files.createDirectories(dir);
            Files.copy(file.getInputStream(), dir.resolve(filename), StandardCopyOption.REPLACE_EXISTING);
            return "/uploads/" + filename;
        } catch (IOException e) {
            throw new RuntimeException("이미지 업로드에 실패했습니다.", e);
        }
    }

    @Transactional(readOnly = true)
    public List<MyPostRes> getMyPosts() {
        Long userIdx = getCurrentUserIdx();
        List<Post> posts = postRepository.findByUser_IdxOrderByCreatedAtDesc(userIdx);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
        return posts.stream().map(post -> {
            String placeName = null;
            String contentTitle = null;
            String contentType = null;
            if (post.getPlaceIdx() != null) {
                Place place = placeRepository.findById(post.getPlaceIdx()).orElse(null);
                if (place != null) {
                    placeName = place.getName();
                    if (place.getContent() != null) {
                        contentTitle = place.getContent().getTitle();
                        contentType = place.getContent().getContentType();
                    }
                }
            }
            String mainImageUrl = post.getImages().isEmpty() ? null : post.getImages().get(0).getImageUrl();
            String createdAt = post.getCreatedAt() != null ? sdf.format(post.getCreatedAt()) : "";
            return MyPostRes.builder()
                    .postIdx(post.getIdx())
                    .placeIdx(post.getPlaceIdx())
                    .placeName(placeName)
                    .contentTitle(contentTitle)
                    .contentType(contentType)
                    .mainImageUrl(mainImageUrl)
                    .createdAt(createdAt)
                    .imageCount(post.getImages().size())
                    .build();
        }).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SnsPostRes> getFeed() {
        List<Post> posts = postRepository.findAllByOrderByCreatedAtDesc();
        return posts.stream().map(post -> {
            String placeName = null;
            String contentTitle = null;
            String contentType = null;
            if (post.getPlaceIdx() != null) {
                Place place = placeRepository.findById(post.getPlaceIdx()).orElse(null);
                if (place != null) {
                    placeName = place.getName();
                    if (place.getContent() != null) {
                        contentTitle = place.getContent().getTitle();
                        contentType = place.getContent().getContentType();
                    }
                }
            }
            String mainImageUrl = post.getImages().isEmpty() ? null : post.getImages().get(0).getImageUrl();
            long createdAtMs = post.getCreatedAt() != null ? post.getCreatedAt().getTime() : 0L;
            return SnsPostRes.builder()
                    .postIdx(post.getIdx())
                    .placeIdx(post.getPlaceIdx())
                    .placeName(placeName)
                    .contentTitle(contentTitle)
                    .contentType(contentType)
                    .userName(post.getUser().getName())
                    .title(post.getTitle())
                    .description(post.getDescription())
                    .mainImageUrl(mainImageUrl)
                    .createdAtMs(createdAtMs)
                    .imageCount(post.getImages().size())
                    .build();
        }).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SnsPostRes> getPostsByUser(Long userIdx) {
        List<Post> posts = postRepository.findByUser_IdxOrderByCreatedAtDesc(userIdx);
        return posts.stream().map(post -> {
            String placeName = null;
            String contentTitle = null;
            String contentType = null;
            if (post.getPlaceIdx() != null) {
                Place place = placeRepository.findById(post.getPlaceIdx()).orElse(null);
                if (place != null) {
                    placeName = place.getName();
                    if (place.getContent() != null) {
                        contentTitle = place.getContent().getTitle();
                        contentType = place.getContent().getContentType();
                    }
                }
            }
            String mainImageUrl = post.getImages().isEmpty() ? null : post.getImages().get(0).getImageUrl();
            long createdAtMs = post.getCreatedAt() != null ? post.getCreatedAt().getTime() : 0L;
            return SnsPostRes.builder()
                    .postIdx(post.getIdx())
                    .placeIdx(post.getPlaceIdx())
                    .placeName(placeName)
                    .contentTitle(contentTitle)
                    .contentType(contentType)
                    .userName(post.getUser().getName())
                    .title(post.getTitle())
                    .description(post.getDescription())
                    .mainImageUrl(mainImageUrl)
                    .createdAtMs(createdAtMs)
                    .imageCount(post.getImages().size())
                    .build();
        }).collect(Collectors.toList());
    }

    public PostDetailRes getPostDetail(Long idx) {
        Post post = postRepository.findByIdWithUserAndImages(idx)
                .orElseThrow(() -> new IllegalArgumentException("해당 포스팅을 찾을 수 없습니다."));

        String placeName = null;
        String contentTitle = null;
        String contentType = null;
        if (post.getPlaceIdx() != null) {
            Place place = placeRepository.findById(post.getPlaceIdx()).orElse(null);
            if (place != null) {
                placeName = place.getName();
                if (place.getContent() != null) {
                    contentTitle = place.getContent().getTitle();
                    contentType = place.getContent().getContentType();
                }
            }
        }

        return PostDetailRes.of(post, placeName, contentTitle, contentType);
    }


}