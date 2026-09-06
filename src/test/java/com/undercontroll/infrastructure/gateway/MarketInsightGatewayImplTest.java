package com.undercontroll.infrastructure.gateway;

import com.undercontroll.domain.enums.InsightGenerationStatus;
import com.undercontroll.domain.model.MarketMonthlyInsight;
import com.undercontroll.infrastructure.mapper.MarketInsightMapper;
import com.undercontroll.infrastructure.persistence.entity.MarketMonthlyInsightJpaEntity;
import com.undercontroll.infrastructure.persistence.repository.MarketMonthlyInsightJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MarketInsightGatewayImplTest {

    @Mock
    private MarketMonthlyInsightJpaRepository repository;

    @Mock
    private MarketInsightMapper mapper;

    @InjectMocks
    private MarketInsightGatewayImpl gateway;

    @Test
    @DisplayName("finds a successful insight by bucket")
    void findSuccessful() {
        MarketMonthlyInsightJpaEntity entity = MarketMonthlyInsightJpaEntity.builder()
                .bucketKey("2026-08")
                .status(InsightGenerationStatus.SUCCESS)
                .build();
        MarketMonthlyInsight domain = MarketMonthlyInsight.builder()
                .bucketKey("2026-08")
                .status(InsightGenerationStatus.SUCCESS)
                .build();
        when(repository.findByBucketKeyAndStatus("2026-08", InsightGenerationStatus.SUCCESS))
                .thenReturn(Optional.of(entity));
        when(mapper.toDomain(entity)).thenReturn(domain);

        Optional<MarketMonthlyInsight> found = gateway.findSuccessfulByBucketKey("2026-08");

        assertTrue(found.isPresent());
        assertEquals("2026-08", found.get().getBucketKey());
    }
}
