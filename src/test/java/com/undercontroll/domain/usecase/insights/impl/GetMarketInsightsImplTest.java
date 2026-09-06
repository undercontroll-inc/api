package com.undercontroll.domain.usecase.insights.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.undercontroll.domain.enums.InsightGenerationStatus;
import com.undercontroll.application.dto.insights.MarketInsightsResponse;
import com.undercontroll.domain.gateway.MarketInsightGateway;
import com.undercontroll.domain.gateway.MarketViewGateway;
import com.undercontroll.domain.model.MarketMonthlyInsight;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetMarketInsightsImplTest {

    @Mock
    private MarketViewGateway marketViewGateway;

    @Mock
    private MarketInsightGateway marketInsightGateway;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private GetMarketInsightsImpl getMarketInsights;

    @Test
    @DisplayName("returns empty list when bucket does not exist")
    void emptyWhenNoBucket() {
        when(marketViewGateway.findCurrentBucketKey()).thenReturn(Optional.empty());

        MarketInsightsResponse response = getMarketInsights.execute();

        assertTrue(response.insights().isEmpty());
        verify(marketInsightGateway, never()).findSuccessfulByBucketKey(org.mockito.ArgumentMatchers.any());
    }

    @Test
    @DisplayName("returns empty list when monthly batch was not generated yet")
    void emptyWhenNoSuccessfulBatch() {
        when(marketViewGateway.findCurrentBucketKey()).thenReturn(Optional.of("2026-08"));
        when(marketInsightGateway.findSuccessfulByBucketKey("2026-08")).thenReturn(Optional.empty());

        MarketInsightsResponse response = getMarketInsights.execute();

        assertTrue(response.insights().isEmpty());
    }

    @Test
    @DisplayName("maps stored payload when a successful batch exists")
    void returnsStoredPayload() throws Exception {
        String json = """
                {
                  "periodo": { "bucket_atual": "2026-08", "bucket_comparacao": "2026-07" },
                  "cobertura_match": { "nivel_1_exato": 1, "nivel_2_marca_categoria": 0, "nivel_3_categoria": 0, "sem_match": 0 },
                  "insights": [],
                  "produtos_em_alta": [],
                  "limitacoes": ["Os dados refletem popularidade e preço anunciado, não volume de vendas."]
                }
                """;
        ObjectMapper realMapper = new ObjectMapper();
        when(marketViewGateway.findCurrentBucketKey()).thenReturn(Optional.of("2026-08"));
        when(marketInsightGateway.findSuccessfulByBucketKey("2026-08")).thenReturn(Optional.of(
                MarketMonthlyInsight.builder()
                        .bucketKey("2026-08")
                        .status(InsightGenerationStatus.SUCCESS)
                        .payload(json)
                        .generatedAt(LocalDateTime.of(2026, 8, 1, 4, 0))
                        .build()
        ));
        when(objectMapper.readValue(json, com.undercontroll.domain.model.insight.InsightsPayload.class))
                .thenReturn(realMapper.readValue(json, com.undercontroll.domain.model.insight.InsightsPayload.class));

        MarketInsightsResponse response = getMarketInsights.execute();

        assertEquals("2026-08", response.bucketKey());
        assertEquals(1, response.matchCoverage().exact());
        assertEquals(1, response.limitations().size());
    }

    @Test
    @DisplayName("returns empty list when stored JSON cannot be parsed")
    void emptyOnInvalidPayload() throws Exception {
        when(marketViewGateway.findCurrentBucketKey()).thenReturn(Optional.of("2026-08"));
        when(marketInsightGateway.findSuccessfulByBucketKey("2026-08")).thenReturn(Optional.of(
                MarketMonthlyInsight.builder()
                        .bucketKey("2026-08")
                        .status(InsightGenerationStatus.SUCCESS)
                        .payload("{not-json")
                        .build()
        ));
        when(objectMapper.readValue("{not-json", com.undercontroll.domain.model.insight.InsightsPayload.class))
                .thenThrow(new RuntimeException("bad json"));

        MarketInsightsResponse response = getMarketInsights.execute();

        assertTrue(response.insights().isEmpty());
    }
}
