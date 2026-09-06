package com.undercontroll.infrastructure.scheduling;

import com.undercontroll.domain.usecase.insights.GenerateMonthlyInsightsPort;
import com.undercontroll.infrastructure.config.InsightsProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MonthlyInsightsScheduler {

    private final GenerateMonthlyInsightsPort generateMonthlyInsightsPort;
    private final InsightsProperties insightsProperties;

    @Scheduled(cron = "${undercontroll.insights.cron:0 0 4 1 * *}")
    public void generateMonthlyInsights() {
        log.info("Starting monthly insights job with provider={}", insightsProperties.resolvedProvider());
        generateMonthlyInsightsPort.execute(false);
    }
}
