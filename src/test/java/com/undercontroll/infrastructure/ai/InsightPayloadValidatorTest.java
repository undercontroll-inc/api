package com.undercontroll.infrastructure.ai;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.undercontroll.domain.model.insight.InsightPromptContext;
import com.undercontroll.domain.model.insight.InsightsPayload;
import com.undercontroll.domain.model.market.MatchCoverage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class InsightPayloadValidatorTest {

    private final InsightPayloadValidator validator = new InsightPayloadValidator(7);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("accepts a payload whose evidence numbers exist in the tool results")
    void acceptsTraceablePayload() {
        EvidenceIndex evidence = new EvidenceIndex(objectMapper);
        evidence.ingest(Map.of("price_delta_pct", 11.11, "avg_price_median", 599.0, "product_count", 2));
        InsightPromptContext context = new InsightPromptContext("2026-08", "2026-07", MatchCoverage.empty(), Set.of());

        assertDoesNotThrow(() -> validator.validate(validPayload(), context, evidence));
    }

    @Test
    @DisplayName("rejects invented numbers and sales language")
    void rejectsHallucinationAndSalesLanguage() {
        EvidenceIndex evidence = new EvidenceIndex(objectMapper);
        evidence.ingest(Map.of("price_delta_pct", 11.11));
        InsightPromptContext context = new InsightPromptContext("2026-08", "2026-07", MatchCoverage.empty(), Set.of());

        InsightsPayload invented = withEvidence(Map.of("price_delta_pct", 99.9));
        assertThrows(IllegalArgumentException.class, () -> validator.validate(invented, context, evidence));

        InsightsPayload sales = withText(pad("A marca vendeu 1000 unidades no mês"));
        assertThrows(IllegalArgumentException.class, () -> validator.validate(sales, context, evidence));

        InsightsPayload tooMany = new InsightsPayload(
                new InsightsPayload.Periodo("2026-08", "2026-07"),
                InsightsPayload.CoberturaMatch.from(MatchCoverage.empty()),
                Stream.generate(() -> insight("ALERTA_PRECO", pad("ok"), Map.of("price_delta_pct", 11.11)))
                        .limit(8)
                        .toList(),
                List.of(),
                List.of("lim")
        );
        assertThrows(IllegalArgumentException.class, () -> validator.validate(tooMany, context, evidence));

        InsightsPayload badType = new InsightsPayload(
                new InsightsPayload.Periodo("2026-08", "2026-07"),
                InsightsPayload.CoberturaMatch.from(MatchCoverage.empty()),
                List.of(insight("INVALID", pad("ok"), Map.of("price_delta_pct", 11.11))),
                List.of(),
                List.of("lim")
        );
        assertThrows(IllegalArgumentException.class, () -> validator.validate(badType, context, evidence));

        InsightsPayload legacyType = new InsightsPayload(
                new InsightsPayload.Periodo("2026-08", "2026-07"),
                InsightsPayload.CoberturaMatch.from(MatchCoverage.empty()),
                List.of(insight("PRECO_ALTA", pad("ok"), Map.of("price_delta_pct", 11.11))),
                List.of(),
                List.of("lim")
        );
        assertThrows(IllegalArgumentException.class, () -> validator.validate(legacyType, context, evidence));

        assertThrows(IllegalArgumentException.class, () -> validator.validate(null, context, evidence));
    }

    @Test
    @DisplayName("rejects duplicate types and out-of-range text")
    void rejectsDuplicateTypesAndTextLength() {
        EvidenceIndex evidence = new EvidenceIndex(objectMapper);
        evidence.ingest(Map.of("price_delta_pct", 11.11));
        InsightPromptContext context = new InsightPromptContext("2026-08", "2026-07", MatchCoverage.empty(), Set.of());

        InsightsPayload duplicate = new InsightsPayload(
                new InsightsPayload.Periodo("2026-08", "2026-07"),
                InsightsPayload.CoberturaMatch.from(MatchCoverage.empty()),
                List.of(
                        insight("ALERTA_PRECO", pad("primeiro"), Map.of("price_delta_pct", 11.11)),
                        insight("ALERTA_PRECO", pad("segundo"), Map.of("price_delta_pct", 11.11))
                ),
                List.of(),
                List.of("lim")
        );
        assertThrows(IllegalArgumentException.class, () -> validator.validate(duplicate, context, evidence));

        InsightsPayload shortText = withText("texto curto");
        assertThrows(IllegalArgumentException.class, () -> validator.validate(shortText, context, evidence));

        InsightsPayload longText = withText("x".repeat(1201));
        assertThrows(IllegalArgumentException.class, () -> validator.validate(longText, context, evidence));

        InsightsPayload longAction = new InsightsPayload(
                new InsightsPayload.Periodo("2026-08", "2026-07"),
                InsightsPayload.CoberturaMatch.from(MatchCoverage.empty()),
                List.of(new InsightsPayload.InsightItem(
                        pad("ok"),
                        "ALERTA_PRECO",
                        "MLB-MICROWAVES",
                        "Micro-ondas",
                        new InsightsPayload.Evidencia("vw_market_price_movement", Map.of("price_delta_pct", 11.11)),
                        "HIGH",
                        "NIVEL_3_CATEGORIA",
                        "a".repeat(281)
                )),
                List.of(),
                List.of("lim")
        );
        assertThrows(IllegalArgumentException.class, () -> validator.validate(longAction, context, evidence));
    }

    @Test
    @DisplayName("rejects internal jargon such as bucket and ISO month codes in visible copy")
    void rejectsTechnicalJargon() {
        EvidenceIndex evidence = new EvidenceIndex(objectMapper);
        evidence.ingest(Map.of("price_delta_pct", 11.11));
        InsightPromptContext context = new InsightPromptContext("2026-08", "2026-07", MatchCoverage.empty(), Set.of());

        InsightsPayload bucketInText = withText(pad("No bucket atual os micro-ondas subiram de preço"));
        assertThrows(IllegalArgumentException.class, () -> validator.validate(bucketInText, context, evidence));

        InsightsPayload isoMonth = withText(pad("Em 2026-08 os micro-ondas subiram de preço no Mercado Livre"));
        assertThrows(IllegalArgumentException.class, () -> validator.validate(isoMonth, context, evidence));

        InsightsPayload bucketInAction = new InsightsPayload(
                new InsightsPayload.Periodo("2026-08", "2026-07"),
                InsightsPayload.CoberturaMatch.from(MatchCoverage.empty()),
                List.of(new InsightsPayload.InsightItem(
                        pad("Micro-ondas subiram de preço neste mês"),
                        "ALERTA_PRECO",
                        "MLB-MICROWAVES",
                        "Micro-ondas",
                        new InsightsPayload.Evidencia("vw_market_price_movement", Map.of("price_delta_pct", 11.11)),
                        "HIGH",
                        "NIVEL_3_CATEGORIA",
                        "Revisar peça no bucket 2026-08."
                )),
                List.of(),
                List.of("lim")
        );
        assertThrows(IllegalArgumentException.class, () -> validator.validate(bucketInAction, context, evidence));
    }

    @Test
    @DisplayName("rejects empty limitations and wrong coverage")
    void rejectsMissingLimitationsAndCoverage() {
        EvidenceIndex evidence = new EvidenceIndex(objectMapper);
        InsightPromptContext context = new InsightPromptContext("2026-08", "2026-07", new MatchCoverage(1, 0, 0, 0), Set.of());
        InsightsPayload payload = new InsightsPayload(
                new InsightsPayload.Periodo("2026-08", "2026-07"),
                InsightsPayload.CoberturaMatch.from(MatchCoverage.empty()),
                List.of(),
                List.of(),
                List.of()
        );
        assertThrows(IllegalArgumentException.class, () -> validator.validate(payload, context, evidence));
    }

    private InsightsPayload validPayload() {
        return new InsightsPayload(
                new InsightsPayload.Periodo("2026-08", "2026-07"),
                InsightsPayload.CoberturaMatch.from(MatchCoverage.empty()),
                List.of(insight("ALERTA_PRECO", pad("Micro-ondas subiram de preço"), Map.of(
                        "price_delta_pct", 11.11, "avg_price_median", 599.0, "product_count", 2))),
                List.of(),
                List.of("Os dados refletem popularidade e preço anunciado, não volume de vendas.")
        );
    }

    private InsightsPayload withEvidence(Map<String, Object> campos) {
        return new InsightsPayload(
                new InsightsPayload.Periodo("2026-08", "2026-07"),
                InsightsPayload.CoberturaMatch.from(MatchCoverage.empty()),
                List.of(insight("ALERTA_PRECO", pad("Micro-ondas subiram de preço"), campos)),
                List.of(),
                List.of("Os dados refletem popularidade e preço anunciado, não volume de vendas.")
        );
    }

    private InsightsPayload withText(String texto) {
        return new InsightsPayload(
                new InsightsPayload.Periodo("2026-08", "2026-07"),
                InsightsPayload.CoberturaMatch.from(MatchCoverage.empty()),
                List.of(insight("ALERTA_PRECO", texto, Map.of("price_delta_pct", 11.11))),
                List.of(),
                List.of("Os dados refletem popularidade e preço anunciado, não volume de vendas.")
        );
    }

    private InsightsPayload.InsightItem insight(String tipo, String texto, Map<String, Object> campos) {
        return new InsightsPayload.InsightItem(
                texto,
                tipo,
                "MLB-MICROWAVES",
                "Micro-ondas",
                new InsightsPayload.Evidencia("vw_market_price_movement", campos),
                "HIGH",
                "NIVEL_3_CATEGORIA",
                "Revisar o preço do conserto."
        );
    }

    private static String pad(String seed) {
        StringBuilder text = new StringBuilder(seed);
        String filler = " A assistência já conserta essa linha e o dado de popularidade reforça estoque de peça.";
        while (text.length() < 280) {
            text.append(filler);
        }
        return text.toString();
    }
}
