package com.undercontroll.domain.gateway;

import com.undercontroll.domain.model.insight.InsightPromptContext;
import com.undercontroll.domain.model.insight.InsightsPayload;

public interface InsightsLlmGateway {

    InsightsPayload generate(InsightPromptContext context);
}
