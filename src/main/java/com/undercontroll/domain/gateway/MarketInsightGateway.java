package com.undercontroll.domain.gateway;

import com.undercontroll.domain.model.MarketMonthlyInsight;

import java.util.Optional;

public interface MarketInsightGateway {

    Optional<MarketMonthlyInsight> findByBucketKey(String bucketKey);

    Optional<MarketMonthlyInsight> findSuccessfulByBucketKey(String bucketKey);

    MarketMonthlyInsight save(MarketMonthlyInsight insight);
}
