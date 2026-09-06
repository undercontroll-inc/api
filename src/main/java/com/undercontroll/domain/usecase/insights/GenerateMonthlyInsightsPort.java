package com.undercontroll.domain.usecase.insights;

public interface GenerateMonthlyInsightsPort {

    InsightGenerationResult execute(boolean force);
}
