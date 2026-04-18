package com.setjetting.api.domain.place.model;

import com.setjetting.api.common.model.BaseEntity;

import com.setjetting.api.domain.content.model.Content;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Place extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PLACE_IDX")
    private Long idx;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CONTENT_IDX", nullable = false)
    private Content content; // 어떤 작품의 촬영지인지 연결

    @Column(name = "NAME", nullable = false, length = 100)
    private String name;

    @Column(name = "ENGLISH_NAME", length = 100)
    private String englishName;

    @Column(name = "ADDRESS", nullable = false)
    private String address;

    @Column(name = "LATITUDE", nullable = false)
    private Double latitude;

    @Column(name = "LONGITUDE", nullable = false)
    private Double longitude;

    @Column(name = "PLACE_IMAGE_URL")
    private String placeImageUrl;

    @Column(name = "DESCRIPTION", columnDefinition = "TEXT")
    private String description;
}