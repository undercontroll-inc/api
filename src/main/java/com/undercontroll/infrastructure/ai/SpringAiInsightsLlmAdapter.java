package com.undercontroll.infrastructure.ai;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.undercontroll.domain.gateway.InsightsLlmGateway;
import com.undercontroll.domain.model.insight.InsightMonthLabel;
import com.undercontroll.domain.model.insight.InsightPromptContext;
import com.undercontroll.domain.model.insight.InsightsPayload;
import com.undercontroll.infrastructure.config.InsightsProperties;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.core.io.Resource;

import java.nio.charset.StandardCharsets;
import java.util.Map;

public class SpringAiInsightsLlmAdapter implements InsightsLlmGateway {

    private final ChatClient insightsChatClient;
    private final ObjectMapper objectMapper;
    private final InsightPayloadValidator insightPayloadValidator;
    private final InsightsProperties insightsProperties;
    private final Resource userPrompt;
    private final MarketInsightTools marketInsightTools;

    public SpringAiInsightsLlmAdapter(
            ChatClient insightsChatClient,
            ObjectMapper objectMapper,
            InsightPayloadValidator insightPayloadValidator,
            InsightsProperties insightsProperties,
            Resource userPrompt,
            MarketInsightTools marketInsightTools
    ) {
        this.insightsChatClient = insightsChatClient;
        this.objectMapper = objectMapper;
        this.insightPayloadValidator = insightPayloadValidator;
        this.insightsProperties = insightsProperties;
        this.userPrompt = userPrompt;
        this.marketInsightTools = marketInsightTools;
    }

    @Override
    public InsightsPayload generate(InsightPromptContext context) {
        EvidenceIndex evidence = new EvidenceIndex(objectMapper);
        InsightGenerationContext.State state = InsightGenerationContext.State.from(context, evidence);
        InsightsPayload payload = invokeWithRetry(context, state);
        insightPayloadValidator.validate(payload, context, evidence);
        return payload;
    }

    private InsightsPayload invokeWithRetry(InsightPromptContext context, InsightGenerationContext.State state) {
        try {
            return invokeModel(context, state);
        } catch (RuntimeException first) {
            try {
                return invokeModel(context, state);
            } catch (RuntimeException ignored) {
                throw first;
            }
        }
    }

    private InsightsPayload invokeModel(InsightPromptContext context, InsightGenerationContext.State state) {
        String comparisonKey = context.comparisonBucketKey();
        InsightsPayload payload = insightsChatClient.prompt()
                .toolContext(Map.of(InsightGenerationContext.KEY, state))
                .user(user -> user.text(userPrompt, StandardCharsets.UTF_8)
                        .param("mesAtual", InsightMonthLabel.of(context.bucketKey()))
                        .param("mesComparacao", InsightMonthLabel.of(comparisonKey))
                        .param("bucketAtual", context.bucketKey())
                        .param("bucketComparacao", comparisonKey == null ? "null" : comparisonKey)
                        .param("maxInsights", insightsProperties.getMaxInsights())
                        .param("grounding", groundingJson(state)))
                .call()
                .entity(InsightsPayload.class);
        if (payload == null) {
            throw new IllegalStateException("LLM returned an empty insights payload");
        }
        return payload;
    }

    private String groundingJson(InsightGenerationContext.State state) {
        try {
            return objectMapper.writeValueAsString(marketInsightTools.grounding(state));
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Failed to serialize insight grounding", ex);
        }
    }
}
