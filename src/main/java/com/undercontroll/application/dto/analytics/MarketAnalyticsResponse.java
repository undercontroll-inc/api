package com.undercontroll.application.dto.analytics;

import java.util.List;

public record MarketAnalyticsResponse(
        String bucketKey,
        long totalProducts,
        long brandsAnalyzed,
        List<BrandHighlight> topBrands,
        List<CategoryHighlight> topCategories
) {
    public static MarketAnalyticsResponse empty() {
        return new MarketAnalyticsResponse(null, 0L, 0L, List.of(), List.of());
    }

    public record BrandHighlight(
            String name,
            String slug,
            long productCount,
            Integer bestRank,
            Double avgScore
    ) {
    }

    public record CategoryHighlight(
            String domainId,
            String name,
            long productCount,
            Double avgScore,
            Long risingCount
    ) {
    }
}
