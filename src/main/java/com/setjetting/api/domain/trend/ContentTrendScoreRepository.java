package com.setjetting.api.domain.trend;

import com.setjetting.api.domain.trend.model.ContentTrendScore;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ContentTrendScoreRepository extends JpaRepository<ContentTrendScore, Long> {
    List<ContentTrendScore> findTop5ByOrderByTrendScoreDesc();
}
