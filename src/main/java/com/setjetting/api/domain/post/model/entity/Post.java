package com.setjetting.api.domain.post.model.entity;

import com.setjetting.api.common.model.BaseEntity;
import com.setjetting.api.domain.user.model.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Post extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IDX", nullable = false)
    private Long idx;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_IDX", nullable = false)
    private User user;

    @Column(name = "TITLE", nullable = false, length = 100)
    private String title;

    @Column(name = "DESCRIPTION", columnDefinition = "TEXT")
    private String description;

    @Column(name = "PLACE_IDX")
    private Long placeIdx;

    // 포스트가 삭제되면 하위 이미지도 함께 삭제되도록 Cascade
    @Builder.Default
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostImage> images = new ArrayList<>();

    // 연관관계 편의 메서드
    public void addImage(PostImage postImage) {
        this.images.add(postImage);
        postImage.assignPost(this);
    }
}