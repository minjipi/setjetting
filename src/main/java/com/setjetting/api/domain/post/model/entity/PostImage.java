package com.setjetting.api.domain.post.model.entity;

import com.setjetting.api.common.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class PostImage extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IDX", nullable = false)
    private Long idx;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "POST_IDX", nullable = false)
    private Post post;

    @Column(name = "IMAGE_URL", nullable = false, length = 300)
    private String imageUrl;

    @Column(name = "DESCRIPTION", length = 200)
    private String description;

    public void assignPost(Post post) {
        this.post = post;
    }
}