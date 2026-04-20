package com.setjetting.api.domain.trend;

import com.setjetting.api.domain.trend.model.ContentActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface ContentActivityRepository extends JpaRepository<ContentActivity, Long> {
    List<ContentActivity> findByCreatedAtAfter(LocalDateTime after);
}
