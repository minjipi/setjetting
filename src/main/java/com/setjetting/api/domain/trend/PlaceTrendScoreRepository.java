package com.setjetting.api.domain.trend;

import com.setjetting.api.domain.trend.model.PlaceTrendScore;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PlaceTrendScoreRepository extends JpaRepository<PlaceTrendScore, Long> {
    List<PlaceTrendScore> findTop5ByOrderByTrendScoreDesc();
}
