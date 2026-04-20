package com.setjetting.api.domain.bookmark.model;

import com.setjetting.api.common.model.BaseEntity;
import com.setjetting.api.domain.place.model.entity.Place;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(
    name = "bookmark",
    uniqueConstraints = @UniqueConstraint(name = "uk_bookmark_user_place", columnNames = {"USER_IDX", "PLACE_IDX"})
)
public class Bookmark extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IDX")
    private Long idx;

    @Column(name = "USER_IDX", nullable = false)
    private Long userIdx;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PLACE_IDX", nullable = false)
    private Place place;

    public static Bookmark of(Long userIdx, Place place) {
        return Bookmark.builder()
                .userIdx(userIdx)
                .place(place)
                .build();
    }
}
