package com.undercontroll.infrastructure.ai;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.undercontroll.domain.gateway.InsightsLlmGateway;
import com.undercontroll.domain.model.insight.InsightMonthLabel;
import com.undercontroll.domain.model.insight.InsightPromptContext;
import com.undercontroll.domain.model.insight.InsightsPayload;
import com.undercontroll.infrastructure.config.InsightsProperties;
import com.undercontroll.infrastructure.logging.LogTiming;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.core.io.Resource;

import java.nio.charset.StandardCharsets;

@Slf4j
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
        long started = System.nanoTime();
        String bucketKey = context.bucketKey();
        log.info(
                "Generating insights bucketKey={} comparison={} provider={} model={}",
                bucketKey,
                context.comparisonBucketKey(),
                insightsProperties.resolvedProvider(),
                insightsProperties.activeModel()
        );
        try {
            EvidenceIndex evidence = new EvidenceIndex(objectMapper);
            InsightGenerationContext.State state = InsightGenerationContext.State.from(context, evidence);
            InsightsPayload payload = insightsChatClient.prompt()
                    .user(user -> user.text(userPrompt, StandardCharsets.UTF_8)
                            .param("mesAtual", InsightMonthLabel.of(context.bucketKey()))
                            .param("mesComparacao", InsightMonthLabel.of(context.comparisonBucketKey()))
                            .param("bucketAtual", context.bucketKey())
                            .param("bucketComparacao", context.comparisonBucketKey() == null ? "null" : context.comparisonBucketKey())
                            .param("maxInsights", insightsProperties.getMaxInsights())
                            .param("grounding", groundingJson(state)))
                    .call()
                    .entity(InsightsPayload.class);
            if (payload == null) {
                throw new IllegalStateException("LLM returned an empty insights payload");
            }
            insightPayloadValidator.validate(payload, context, evidence);
            log.info("Generated insights bucketKey={} durationMs={}", bucketKey, LogTiming.millisSince(started));
            return payload;
        } catch (RuntimeException ex) {
            log.warn(
                    "Insight generation failed bucketKey={} durationMs={} cause={}",
                    bucketKey,
                    LogTiming.millisSince(started),
                    ex.toString()
            );
            throw ex;
        }
    }

    private String groundingJson(InsightGenerationContext.State state) {
        try {
            return objectMapper.writeValueAsString(marketInsightTools.grounding(state));
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Failed to serialize insight grounding", ex);
        }
    }
}
