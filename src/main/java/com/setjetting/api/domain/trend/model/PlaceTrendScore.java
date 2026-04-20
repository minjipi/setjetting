package com.setjetting.api.domain.trend.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "PLACE_TREND_SCORE")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class PlaceTrendScore {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IDX")
    private Long idx;

    @Column(name = "PLACE_IDX", nullable = false, unique = true)
    private Long placeIdx;

    @Column(name = "TREND_SCORE", nullable = false)
    private Double trendScore;

    @Column(name = "RAW_SCORE", nullable = false)
    private Double rawScore;

    @Column(name = "COMPUTED_AT", nullable = false)
    private LocalDateTime computedAt;
}
