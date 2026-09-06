package com.undercontroll.domain.market;

import com.undercontroll.domain.model.market.MarketCategorySummary;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ApplianceTypeDomainMapTest {

    @Test
    @DisplayName("maps known CRM types to Mercado Livre domain_id")
    void mapsKnownTypes() {
        assertEquals("MLB-MICROWAVES", ApplianceTypeDomainMap.resolve("Microondas", List.of()));
        assertEquals("MLB-AIR_FRYERS", ApplianceTypeDomainMap.resolve("Fritadeira", List.of()));
        assertEquals("MLB-BLENDERS", ApplianceTypeDomainMap.resolve("Liquidificador", List.of()));
    }

    @Test
    @DisplayName("falls back to category name when type is unknown")
    void fallbackToCategoryName() {
        MarketCategorySummary category = new MarketCategorySummary(
                "MLB-FANS", "2026-08", "Ventilador", 2L, 1L, 70.0, 80.0,
                100.0, 90.0, 120.0, 5.0, 1.0, 10.0, 1L, 0L, 1L);
        assertEquals("MLB-FANS", ApplianceTypeDomainMap.resolve("Ventilador de mesa", List.of(category)));
    }

    @Test
    @DisplayName("returns null when type cannot be mapped")
    void unknownType() {
        assertNull(ApplianceTypeDomainMap.resolve("Geladeira", List.of()));
    }
}
