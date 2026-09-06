package com.undercontroll.infrastructure.scheduling;

import com.undercontroll.domain.usecase.insights.GenerateMonthlyInsightsPort;
import com.undercontroll.infrastructure.config.InsightsProperties;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class MonthlyInsightsSchedulerTest {

    @Test
    @DisplayName("delegates generation to the use case")
    void delegates() {
        GenerateMonthlyInsightsPort port = mock(GenerateMonthlyInsightsPort.class);
        MonthlyInsightsScheduler scheduler = new MonthlyInsightsScheduler(port, new InsightsProperties());

        scheduler.generateMonthlyInsights();

        verify(port, times(1)).execute(false);
    }
}
