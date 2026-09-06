package com.undercontroll.domain.model.market;

public record MarketBrandSummary(
        String brandSlug,
        String bucketKey,
        String brandName,
        Long productCount,
        Long domainCount,
        Double avgScore,
        Integer bestRank,
        Double avgPriceMedian,
        Double priceFloor,
        Double priceCeiling,
        Double avgPriceDeltaPct,
        Double avgDiscountPct,
        Long risingCount
) {
}
