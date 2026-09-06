package com.undercontroll.infrastructure.ai;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AnaWebSearchNeedTest {

    @Test
    @DisplayName("detects manual, recall and tip questions")
    void matches() {
        assertTrue(AnaWebSearchNeed.matches("Cadê o manual da airfryer?"));
        assertTrue(AnaWebSearchNeed.matches("Tem recall desse micro-ondas?"));
        assertTrue(AnaWebSearchNeed.matches("Me dá uma dica de limpeza"));
        assertFalse(AnaWebSearchNeed.matches("Quais consertos estão abertos?"));
        assertFalse(AnaWebSearchNeed.matches(null));
    }
}
