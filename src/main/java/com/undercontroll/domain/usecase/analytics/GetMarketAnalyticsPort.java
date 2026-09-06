package com.undercontroll.domain.usecase.analytics;

import com.undercontroll.application.dto.analytics.MarketAnalyticsResponse;

public interface GetMarketAnalyticsPort {
    MarketAnalyticsResponse execute();
}
