package com.undercontroll.domain.usecase.insights.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.undercontroll.application.dto.insights.InsightsResponseMapper;
import com.undercontroll.application.dto.insights.MarketInsightsResponse;
import com.undercontroll.domain.gateway.MarketInsightGateway;
import com.undercontroll.domain.gateway.MarketViewGateway;
import com.undercontroll.domain.model.MarketMonthlyInsight;
import com.undercontroll.domain.model.insight.InsightsPayload;
import com.undercontroll.domain.usecase.insights.GetMarketInsightsPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetMarketInsightsImpl implements GetMarketInsightsPort {

    private final MarketViewGateway marketViewGateway;
    private final MarketInsightGateway marketInsightGateway;
    private final ObjectMapper objectMapper;

    @Override
    @Cacheable(value = "marketInsights", key = "'current'")
    public MarketInsightsResponse execute() {
        return marketViewGateway.findCurrentBucketKey()
                .flatMap(marketInsightGateway::findSuccessfulByBucketKey)
                .map(this::toResponse)
                .orElseGet(MarketInsightsResponse::empty);
    }

    private MarketInsightsResponse toResponse(MarketMonthlyInsight insight) {
        try {
            InsightsPayload payload = objectMapper.readValue(insight.getPayload(), InsightsPayload.class);
            String generatedAt = insight.getGeneratedAt() == null ? null : insight.getGeneratedAt().toString();
            return InsightsResponseMapper.from(payload, generatedAt);
        } catch (Exception ex) {
            log.warn("Failed to deserialize stored insights for bucket {}: {}", insight.getBucketKey(), ex.getMessage());
            return MarketInsightsResponse.empty();
        }
    }
}
