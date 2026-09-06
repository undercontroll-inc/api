package com.undercontroll.application.controller.impl;

import com.undercontroll.application.controller.MarketAnalyticsApi;
import com.undercontroll.application.dto.analytics.MarketAnalyticsResponse;
import com.undercontroll.domain.usecase.analytics.GetMarketAnalyticsPort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MarketAnalyticsController implements MarketAnalyticsApi {

    private final GetMarketAnalyticsPort getMarketAnalyticsPort;

    @Override
    public ResponseEntity<MarketAnalyticsResponse> getAnalytics() {
        return ResponseEntity.ok(getMarketAnalyticsPort.execute());
    }
}
