package com.undercontroll.domain.model.market;

import java.util.List;

public record MarketSnapshot(
        String bucketKey,
        long totalProducts,
        long brandsAnalyzed,
        List<BrandLine> topBrands,
        List<CategoryLine> topCategories
) {
    public record BrandLine(
            String name,
            String slug,
            long productCount,
            Integer bestRank,
            Double avgScore
    ) {
    }

    public record CategoryLine(
            String domainId,
            String name,
            long productCount,
            Double avgScore,
            Long risingCount
    ) {
    }
}
