package com.setjetting.api.domain.content.model;
import com.setjetting.api.common.model.BaseEntity;
import com.setjetting.api.domain.place.model.entity.Place;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Content extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IDX")
    private Long idx;

    @Column(name = "TITLE", nullable = false, length = 100)
    private String title; // 예: "이태원 클라쓰"

    @Column(name = "ENGLISH_TITLE", length = 100)
    private String englishTitle; // 외국인 타겟이므로 영문 제목 필수 "Itaewon Class"

    @Column(name = "CONTENT_TYPE", nullable = false, length = 20)
    private String contentType; // "DRAMA", "MOVIE", "MUSIC_VIDEO" 등

    @Column(name = "POSTER_IMAGE_URL", nullable = false)
    private String posterImageUrl; // 메인 화면에 보여줄 포스터 이미지

    @Column(name = "DESCRIPTION", columnDefinition = "TEXT")
    private String description; // 작품 간단 설명

    @Column(name = "RELEASE_DATE")
    private LocalDate releaseDate; // 방영/개봉일

    @Builder.Default
    @OneToMany(mappedBy = "content", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Place> places = new ArrayList<>();
}