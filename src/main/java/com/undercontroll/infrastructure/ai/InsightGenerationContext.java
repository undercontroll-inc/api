package com.undercontroll.infrastructure.ai;

import com.undercontroll.domain.model.insight.InsightPromptContext;
import com.undercontroll.domain.model.market.MatchCoverage;
import com.undercontroll.domain.model.market.RepairCatalogItem;
import org.springframework.ai.chat.model.ToolContext;

import java.util.List;
import java.util.Set;

public final class InsightGenerationContext {

    public static final String KEY = "insightGenerationState";

    private InsightGenerationContext() {
    }

    public static State require(ToolContext toolContext) {
        if (toolContext == null || toolContext.getContext() == null) {
            throw new IllegalStateException("Insight generation context is not active");
        }
        Object raw = toolContext.getContext().get(KEY);
        if (raw instanceof State state) {
            return state;
        }
        throw new IllegalStateException("Insight generation context is not active");
    }

    public record State(
            String bucketKey,
            String comparisonBucketKey,
            MatchCoverage coverage,
            Set<String> clientDomainIds,
            List<RepairCatalogItem> catalog,
            EvidenceIndex evidence
    ) {
        public static State from(InsightPromptContext context, EvidenceIndex evidence) {
            return new State(
                    context.bucketKey(),
                    context.comparisonBucketKey(),
                    context.coverage(),
                    context.clientDomainIds() == null ? Set.of() : context.clientDomainIds(),
                    context.catalog() == null ? List.of() : context.catalog(),
                    evidence
            );
        }
    }
}
