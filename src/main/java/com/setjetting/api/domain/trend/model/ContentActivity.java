package com.setjetting.api.domain.trend.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "CONTENT_ACTIVITY", indexes = {
    @Index(name = "idx_ca_content_time", columnList = "CONTENT_IDX, CREATED_AT"),
    @Index(name = "idx_ca_user_content_type", columnList = "USER_ID, CONTENT_IDX, ACTIVITY_TYPE")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ContentActivity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IDX")
    private Long idx;

    @Column(name = "CONTENT_IDX", nullable = false)
    private Long contentIdx;

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
