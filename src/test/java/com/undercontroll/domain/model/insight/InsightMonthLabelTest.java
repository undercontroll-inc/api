package com.undercontroll.domain.model.insight;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InsightMonthLabelTest {

    @Test
    @DisplayName("formats a bucket key as a Portuguese month phrase")
    void formatsKnownMonth() {
        assertEquals("mês de agosto de 2026", InsightMonthLabel.of("2026-08"));
        assertEquals("mês de janeiro de 2026", InsightMonthLabel.of("2026-01"));
        assertEquals("mês de julho de 2026", InsightMonthLabel.of("2026-07"));
        assertEquals("mês de março de 2025", InsightMonthLabel.of("2025-03"));
    }

    @Test
    @DisplayName("returns a fallback when the bucket key is missing or invalid")
    void fallback() {
        assertEquals("mês indisponível", InsightMonthLabel.of(null));
        assertEquals("mês indisponível", InsightMonthLabel.of(" "));
        assertEquals("mês indisponível", InsightMonthLabel.of("indisponível"));
        assertEquals("mês indisponível", InsightMonthLabel.of("2026-13"));
    }
}
