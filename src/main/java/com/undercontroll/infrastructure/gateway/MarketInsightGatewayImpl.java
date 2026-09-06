package com.undercontroll.infrastructure.gateway;

import com.undercontroll.domain.enums.InsightGenerationStatus;
import com.undercontroll.domain.gateway.MarketInsightGateway;
import com.undercontroll.domain.model.MarketMonthlyInsight;
import com.undercontroll.infrastructure.mapper.MarketInsightMapper;
import com.undercontroll.infrastructure.persistence.entity.MarketMonthlyInsightJpaEntity;
import com.undercontroll.infrastructure.persistence.repository.MarketMonthlyInsightJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MarketInsightGatewayImpl implements MarketInsightGateway {

    private final MarketMonthlyInsightJpaRepository repository;
    private final MarketInsightMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public Optional<MarketMonthlyInsight> findByBucketKey(String bucketKey) {
        return repository.findByBucketKey(bucketKey).map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<MarketMonthlyInsight> findSuccessfulByBucketKey(String bucketKey) {
        return repository.findByBucketKeyAndStatus(bucketKey, InsightGenerationStatus.SUCCESS)
                .map(mapper::toDomain);
    }

    @Override
    @Transactional
    public MarketMonthlyInsight save(MarketMonthlyInsight insight) {
        MarketMonthlyInsightJpaEntity saved = repository.save(mapper.toEntityWithId(insight));
        return mapper.toDomain(saved);
    }
}
