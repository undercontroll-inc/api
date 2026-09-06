package com.undercontroll.infrastructure.ai;

import com.undercontroll.domain.model.insight.InsightPromptContext;
import com.undercontroll.domain.model.market.MatchCoverage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.model.ToolContext;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class InsightGenerationContextTest {

    @Test
    @DisplayName("throws when tools are used outside a generation run")
    void requiresActiveContext() {
        ToolContext empty = new ToolContext(Map.of());
        assertThrows(IllegalStateException.class, () -> InsightGenerationContext.require(null));
        assertThrows(IllegalStateException.class, () -> InsightGenerationContext.require(empty));
    }

    @Test
    @DisplayName("reads the generation state from the tool context")
    void readsState() {
        InsightPromptContext prompt = new InsightPromptContext(
                "2026-08", "2026-07", MatchCoverage.empty(), Set.of(), List.of());
        EvidenceIndex evidence = new EvidenceIndex(new com.fasterxml.jackson.databind.ObjectMapper());
        InsightGenerationContext.State state = InsightGenerationContext.State.from(prompt, evidence);

        InsightGenerationContext.State loaded = InsightGenerationContext.require(
                new ToolContext(Map.of(InsightGenerationContext.KEY, state)));

        assertEquals("2026-08", loaded.bucketKey());
    }
}
