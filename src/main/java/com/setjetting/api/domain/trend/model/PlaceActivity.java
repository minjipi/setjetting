package com.setjetting.api.domain.trend.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "PLACE_ACTIVITY", indexes = {
    @Index(name = "idx_pa_place_time", columnList = "PLACE_IDX, CREATED_AT"),
    @Index(name = "idx_pa_user_place_type", columnList = "USER_ID, PLACE_IDX, ACTIVITY_TYPE")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class PlaceActivity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IDX")
    private Long idx;

    @Column(name = "PLACE_IDX", nullable = false)
    private Long placeIdx;

    @Column(name = "USER_ID")
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "ACTIVITY_TYPE", nullable = false, length = 20)
    private ActivityType activityType;

    @Column(name = "CREATED_AT", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void prePersist() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }
}
