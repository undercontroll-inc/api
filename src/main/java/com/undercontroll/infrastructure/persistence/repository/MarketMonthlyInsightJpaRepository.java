package com.undercontroll.infrastructure.persistence.repository;

import com.undercontroll.domain.enums.InsightGenerationStatus;
import com.undercontroll.infrastructure.persistence.entity.MarketMonthlyInsightJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MarketMonthlyInsightJpaRepository extends JpaRepository<MarketMonthlyInsightJpaEntity, Integer> {

    Optional<MarketMonthlyInsightJpaEntity> findByBucketKey(String bucketKey);

    Optional<MarketMonthlyInsightJpaEntity> findByBucketKeyAndStatus(String bucketKey, InsightGenerationStatus status);
}
