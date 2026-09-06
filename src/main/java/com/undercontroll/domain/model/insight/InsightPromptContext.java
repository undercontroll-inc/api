package com.undercontroll.domain.model.insight;

import com.undercontroll.domain.model.market.MatchCoverage;
import com.undercontroll.domain.model.market.RepairCatalogItem;

import java.util.List;
import java.util.Set;

public record InsightPromptContext(
        String bucketKey,
        String comparisonBucketKey,
        MatchCoverage coverage,
        Set<String> clientDomainIds,
        List<RepairCatalogItem> catalog
) {
    public InsightPromptContext {
        clientDomainIds = clientDomainIds == null ? Set.of() : Set.copyOf(clientDomainIds);
        catalog = catalog == null ? List.of() : List.copyOf(catalog);
    }

    public InsightPromptContext(
            String bucketKey,
            String comparisonBucketKey,
            MatchCoverage coverage,
            Set<String> clientDomainIds
    ) {
        this(bucketKey, comparisonBucketKey, coverage, clientDomainIds, List.of());
    }
}
