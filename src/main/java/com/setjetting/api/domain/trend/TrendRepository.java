package com.setjetting.api.domain.trend;

import com.setjetting.api.domain.trend.model.Trend;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrendRepository extends JpaRepository<Trend, Long> {
}
