package com.undercontroll.infrastructure.ai;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.template.st.StTemplateRenderer;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InsightsUserPromptTemplateTest {

    @Test
    @DisplayName("user prompt interpolates month names for visible copy and codes for JSON")
    void rendersMonthLabels() throws IOException {
        String template = new ClassPathResource("prompts/insights-user-prompt.txt")
                .getContentAsString(StandardCharsets.UTF_8);
        String rendered = PromptTemplate.builder()
                .template(template)
                .variables(Map.of(
                        "mesAtual", "mês de agosto de 2026",
                        "mesComparacao", "mês de julho de 2026",
                        "bucketAtual", "2026-08",
                        "bucketComparacao", "2026-07",
                        "maxInsights", 7
                ))
                .renderer(StTemplateRenderer.builder()
                        .startDelimiterToken('<')
                        .endDelimiterToken('>')
                        .build())
                .build()
                .render();

        assertTrue(rendered.startsWith("Gere os insights do mês de agosto de 2026"));
        assertTrue(rendered.contains("mês de julho de 2026"));
        assertTrue(rendered.contains("periodo.bucket_atual = 2026-08"));
        assertTrue(rendered.contains("7"));
        assertTrue(rendered.contains("get_repair_catalog"));
        assertFalse(rendered.contains("<mesAtual>"));
        assertFalse(rendered.contains("<bucketAtual>"));
        assertFalse(rendered.contains("do mês 2026-08"));
        assertFalse(rendered.contains("do bucket"));
    }
}
