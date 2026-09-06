package com.undercontroll.infrastructure.ai;

import com.undercontroll.domain.model.insight.InsightType;
import com.undercontroll.domain.model.insight.InsightsPayload;
import com.undercontroll.domain.model.insight.InsightPromptContext;

import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public class InsightPayloadValidator {

    static final int MIN_TEXT_LENGTH = 280;
    static final int MAX_TEXT_LENGTH = 1200;
    static final int MAX_ACTION_LENGTH = 280;

    static final Set<String> ALLOWED_RELATIONS = Set.of(
            "NIVEL_1_EXATO",
            "NIVEL_2_MARCA_CATEGORIA",
            "NIVEL_3_CATEGORIA",
            "SEM_MATCH"
    );

    private static final Pattern SALES_LANGUAGE = Pattern.compile(
            "vendeu\\s+\\d+|unidades vendidas|l[ií]der de vendas em unidades|volume de vendas",
            Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE
    );

    private static final Pattern TECHNICAL_JARGON = Pattern.compile(
            "\\bbuckets?\\b|bucket[_\\s-]?key|bucket[_\\s-]?atual|bucket[_\\s-]?comparacao"
                    + "|domain_id|vw_market_|get_repair_catalog|get_match_coverage|get_market_snapshot"
                    + "|\\b\\d{4}-\\d{2}\\b",
            Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE
    );

    private final int maxInsights;

    public InsightPayloadValidator(int maxInsights) {
        this.maxInsights = maxInsights;
    }

    public void validate(InsightsPayload payload, InsightPromptContext context, EvidenceIndex evidence) {
        if (payload == null) {
            throw new IllegalArgumentException("Insights payload is required");
        }
        if (payload.limitacoes() == null || payload.limitacoes().isEmpty()) {
            throw new IllegalArgumentException("limitacoes is required");
        }
        if (payload.insights() == null) {
            throw new IllegalArgumentException("insights is required");
        }
        if (payload.insights().size() > maxInsights) {
            throw new IllegalArgumentException("insights exceeds max of " + maxInsights);
        }
        if (payload.periodo() == null
                || !context.bucketKey().equals(payload.periodo().bucketAtual())) {
            throw new IllegalArgumentException("periodo.bucket_atual does not match the current bucket");
        }
        InsightsPayload.CoberturaMatch expected = InsightsPayload.CoberturaMatch.from(context.coverage());
        if (payload.coberturaMatch() == null || !payload.coberturaMatch().equals(expected)) {
            throw new IllegalArgumentException("cobertura_match must copy get_match_coverage");
        }
        Set<String> types = new HashSet<>();
        for (InsightsPayload.InsightItem insight : payload.insights()) {
            if (insight.tipo() != null && !types.add(insight.tipo())) {
                throw new IllegalArgumentException("Duplicate insight tipo: " + insight.tipo());
            }
            validateInsight(insight, evidence);
        }
        for (String limitation : payload.limitacoes()) {
            validateReadableLanguage(limitation);
        }
        if (payload.produtosEmAlta() != null) {
            for (InsightsPayload.ProdutoEmAlta product : payload.produtosEmAlta()) {
                validateSalesLanguage(product.nome());
                validateReadableLanguage(product.nome());
            }
        }
    }

    private void validateInsight(InsightsPayload.InsightItem insight, EvidenceIndex evidence) {
        if (!InsightType.isAllowed(insight.tipo())) {
            throw new IllegalArgumentException("Invalid insight tipo: " + insight.tipo());
        }
        if (insight.relacaoComConsertos() == null || !ALLOWED_RELATIONS.contains(insight.relacaoComConsertos())) {
            throw new IllegalArgumentException("Invalid relacao_com_consertos: " + insight.relacaoComConsertos());
        }
        int textLength = insight.texto() == null ? 0 : insight.texto().length();
        if (textLength < MIN_TEXT_LENGTH || textLength > MAX_TEXT_LENGTH) {
            throw new IllegalArgumentException(
                    "Insight texto must be between " + MIN_TEXT_LENGTH + " and " + MAX_TEXT_LENGTH + " characters");
        }
        int actionLength = insight.acaoSugerida() == null ? 0 : insight.acaoSugerida().length();
        if (actionLength > MAX_ACTION_LENGTH) {
            throw new IllegalArgumentException("Insight acao_sugerida exceeds " + MAX_ACTION_LENGTH + " characters");
        }
        validateSalesLanguage(insight.texto());
        validateSalesLanguage(insight.acaoSugerida());
        validateReadableLanguage(insight.texto());
        validateReadableLanguage(insight.acaoSugerida());
        if (insight.evidencia() == null || insight.evidencia().campos() == null || insight.evidencia().campos().isEmpty()) {
            throw new IllegalArgumentException("Insight evidence is required");
        }
        for (Map.Entry<String, Object> field : insight.evidencia().campos().entrySet()) {
            if (!evidence.contains(field.getValue()) && !evidence.contains(field.getKey())) {
                if (field.getValue() != null && !evidence.contains(String.valueOf(field.getValue()))) {
                    throw new IllegalArgumentException(
                            "Evidence field is not traceable: " + field.getKey() + "=" + field.getValue());
                }
            }
        }
    }

    private void validateSalesLanguage(String text) {
        if (text == null) {
            return;
        }
        if (SALES_LANGUAGE.matcher(text.toLowerCase(Locale.ROOT)).find()) {
            throw new IllegalArgumentException("Insight claims sales volume, which the ETL does not support");
        }
    }

    private void validateReadableLanguage(String text) {
        if (text == null) {
            return;
        }
        if (TECHNICAL_JARGON.matcher(text).find()) {
            throw new IllegalArgumentException("Insight uses internal jargon (write mês de agosto de 2026, not 2026-08 or bucket)");
        }
    }
}
