package com.undercontroll.domain.model.market;

public record MarketPriceMovement(
        String domainId,
        String bucketKey,
        String previousBucketKey,
        Long productCount,
        Long offerCount,
        Long sellerCount,
        Double avgPriceMedian,
        Double previousPriceMedian,
        Double priceDeltaPct,
        Double offerDeltaPct
) {
}
