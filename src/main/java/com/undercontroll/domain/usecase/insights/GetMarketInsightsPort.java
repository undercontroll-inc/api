package com.undercontroll.domain.usecase.insights;

import com.undercontroll.application.dto.insights.MarketInsightsResponse;

public interface GetMarketInsightsPort {
    MarketInsightsResponse execute();
}
