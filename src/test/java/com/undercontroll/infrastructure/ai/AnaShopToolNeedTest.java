package com.undercontroll.infrastructure.ai;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AnaShopToolNeedTest {

    @Test
    @DisplayName("attaches shop tools only for stock, parts and announcements")
    void matches() {
        assertTrue(AnaShopToolNeed.matches("Tem peça acabando no estoque?"));
        assertTrue(AnaShopToolNeed.matches("Qual o último aviso?"));
        assertFalse(AnaShopToolNeed.matches("Me fala do pedido 12"));
        assertFalse(AnaShopToolNeed.matches("Quais consertos estão abertos?"));
        assertFalse(AnaShopToolNeed.matches(null));
    }
}
