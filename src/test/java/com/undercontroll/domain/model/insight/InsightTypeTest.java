package com.undercontroll.domain.model.insight;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InsightTypeTest {

    @Test
    @DisplayName("accepts the seven frontend codes and rejects legacy ones")
    void allowedCodes() {
        assertTrue(InsightType.isAllowed("TENDENCIA_ALTA"));
        assertTrue(InsightType.isAllowed("OPORTUNIDADE_ESTOQUE"));
        assertTrue(InsightType.isAllowed("ALERTA_PRECO"));
        assertTrue(InsightType.isAllowed("OPORTUNIDADE_REPARO"));
        assertTrue(InsightType.isAllowed("DESTAQUE_MARCA"));
        assertTrue(InsightType.isAllowed("ALERTA_MERCADO"));
        assertTrue(InsightType.isAllowed("RECOMENDACAO"));
        assertFalse(InsightType.isAllowed("PRECO_ALTA"));
        assertFalse(InsightType.isAllowed(null));
        assertFalse(InsightType.isAllowed(""));
    }

    @Test
    @DisplayName("maps stored legacy tipos to the new frontend codes")
    void normalizesLegacy() {
        assertEquals("ALERTA_PRECO", InsightType.normalize("PRECO_ALTA"));
        assertEquals("ALERTA_PRECO", InsightType.normalize("PRECO_QUEDA"));
        assertEquals("ALERTA_PRECO", InsightType.normalize("DISPERSAO_PRECO"));
        assertEquals("OPORTUNIDADE_ESTOQUE", InsightType.normalize("OPORTUNIDADE_ESTOQUE"));
        assertEquals("TENDENCIA_ALTA", InsightType.normalize("PRODUTO_ALTA"));
        assertEquals("DESTAQUE_MARCA", InsightType.normalize("MARCA"));
        assertEquals("ALERTA_MERCADO", InsightType.normalize("CONCENTRACAO_CONCORRENCIA"));
        assertEquals("OPORTUNIDADE_REPARO", InsightType.normalize("PERFIL_TECNICO"));
        assertEquals("OPORTUNIDADE_REPARO", InsightType.normalize("CATEGORIA_NAO_ATENDIDA"));
        assertEquals("RECOMENDACAO", InsightType.normalize("RECOMENDACAO"));
        assertEquals("UNKNOWN", InsightType.normalize("UNKNOWN"));
    }
}
