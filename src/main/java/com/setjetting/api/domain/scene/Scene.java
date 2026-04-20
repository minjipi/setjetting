package com.setjetting.api.domain.scene;

import com.setjetting.api.common.model.BaseEntity;
import com.setjetting.api.domain.place.model.entity.Place;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Scene extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SCENE_IDX")
    private Long idx;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PLACE_IDX", nullable = false)
    private Place place;

    @Column(name = "SCENE_IMAGE_URL", nullable = false)
    private String sceneImageUrl; // 실제 장면 스크린샷

    @Column(name = "EPISODE_NUMBER")
    private Integer episodeNumber; // 몇 화

    @Column(name = "APPEAR_TIME")
    private Integer appearTime;

    @Column(name = "SCENE_DESCRIPTION")
    private String sceneDescription; // 장면 상황 설명

    public void assignPlace(Place place) {
        this.place = place;
    }
}