package com.undercontroll.domain.usecase.analytics.impl;

import com.undercontroll.application.dto.analytics.MarketAnalyticsResponse;
import com.undercontroll.domain.gateway.MarketViewGateway;
import com.undercontroll.domain.usecase.analytics.GetMarketAnalyticsPort;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetMarketAnalyticsImpl implements GetMarketAnalyticsPort {

    private static final int TOP_N = 5;

    private final MarketViewGateway marketViewGateway;

    @Override
    @Cacheable(value = "marketAnalytics", key = "'current'")
    public MarketAnalyticsResponse execute() {
        return marketViewGateway.findCurrentBucketKey()
                .map(this::build)
                .orElseGet(MarketAnalyticsResponse::empty);
    }

    private MarketAnalyticsResponse build(String bucketKey) {
        List<MarketAnalyticsResponse.BrandHighlight> brands = marketViewGateway.findTopBrands(bucketKey, TOP_N)
                .stream()
                .map(row -> new MarketAnalyticsResponse.BrandHighlight(
                        row.brandName(),
                        row.brandSlug(),
                        row.productCount() == null ? 0L : row.productCount(),
                        row.bestRank(),
                        row.avgScore()
                ))
                .toList();
        List<MarketAnalyticsResponse.CategoryHighlight> categories = marketViewGateway.findTopCategories(bucketKey, TOP_N)
                .stream()
                .map(row -> new MarketAnalyticsResponse.CategoryHighlight(
                        row.domainId(),
                        row.categoryName(),
                        row.productCount() == null ? 0L : row.productCount(),
                        row.avgScore(),
                        row.risingCount()
                ))
                .toList();
        return new MarketAnalyticsResponse(
                bucketKey,
                marketViewGateway.countCurrentProducts(),
                marketViewGateway.countDistinctBrands(),
                brands,
                categories
        );
    }
}
