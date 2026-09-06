package com.undercontroll.infrastructure.gateway;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class MarketViewGatewayImplTest {

    @Mock
    private NamedParameterJdbcTemplate jdbc;

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private MarketViewGatewayImpl gateway;

    @Test
    @DisplayName("returns empty results when ETL views are missing")
    void emptyWhenViewsMissing() {
        when(jdbc.getJdbcTemplate()).thenReturn(jdbcTemplate);
        when(jdbcTemplate.queryForObject(anyString(), eq(String.class)))
                .thenThrow(new InvalidDataAccessResourceUsageException("missing view"));
        when(jdbcTemplate.queryForObject(anyString(), eq(Long.class)))
                .thenThrow(new InvalidDataAccessResourceUsageException("missing view"));
        when(jdbcTemplate.query(anyString(), any(org.springframework.jdbc.core.RowMapper.class)))
                .thenThrow(new InvalidDataAccessResourceUsageException("missing view"));
        when(jdbc.query(anyString(), any(org.springframework.jdbc.core.namedparam.SqlParameterSource.class), any(org.springframework.jdbc.core.RowMapper.class)))
                .thenThrow(new InvalidDataAccessResourceUsageException("missing view"));
        when(jdbc.queryForObject(anyString(), any(org.springframework.jdbc.core.namedparam.SqlParameterSource.class), eq(String.class)))
                .thenThrow(new InvalidDataAccessResourceUsageException("missing view"));

        assertTrue(gateway.findCurrentBucketKey().isEmpty());
        assertEquals(0L, gateway.countCurrentProducts());
        assertEquals(0L, gateway.countDistinctBrands());
        assertTrue(gateway.findAllCurrentProducts().isEmpty());
        assertTrue(gateway.findTopBrands("2026-08", 5).isEmpty());
        assertTrue(gateway.findTopCategories("2026-08", 5).isEmpty());
        assertTrue(gateway.findPriceMovements("2026-08").isEmpty());
        assertTrue(gateway.findStockOpportunities("2026-08").isEmpty());
        assertTrue(gateway.findRisingProductsHighConfidence().isEmpty());
        assertTrue(gateway.findBrandMomentum("2026-08").isEmpty());
        assertTrue(gateway.findCategorySummary("2026-08").isEmpty());
        assertTrue(gateway.findPriceDispersion().isEmpty());
        assertTrue(gateway.findUncoveredCategories("2026-08", List.of("MLB-MICROWAVES")).isEmpty());
        assertTrue(gateway.findPreviousBucketKey("2026-08").isEmpty());
    }
}
