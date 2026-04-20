package com.setjetting.api.domain.trend;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TrendScheduler {

    private final TrendService trendService;

    @Scheduled(cron = "0 * * * * *") // every hour
    public void computeTrends() {
        log.info("Trend batch computation started");
        trendService.computePlaceTrends();
        trendService.computeContentTrends();
        log.info("Trend batch computation completed");
    }
}
