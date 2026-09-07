package com.undercontroll.infrastructure.ai;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.undercontroll.domain.model.insight.InsightsPayload;
import com.undercontroll.domain.model.market.MarketSnapshot;
import com.undercontroll.domain.model.market.RepairCatalogLine;
import com.undercontroll.domain.model.market.RepairMixItem;

import java.util.List;

public record InsightGrounding(
        @JsonProperty("cobertura_match") InsightsPayload.CoberturaMatch coberturaMatch,
        @JsonProperty("repair_catalog") List<RepairCatalogLine> repairCatalog,
        @JsonProperty("repair_mix") List<RepairMixItem> repairMix,
        @JsonProperty("market_snapshot") Snapshot marketSnapshot
) {
    public record Snapshot(
            long totalProducts,
            long brandsAnalyzed,
            List<MarketSnapshot.BrandLine> topBrands,
            List<MarketSnapshot.CategoryLine> topCategories
    ) {
        static Snapshot from(MarketSnapshot snapshot) {
            if (snapshot == null) {
                return new Snapshot(0L, 0L, List.of(), List.of());
            }
            return new Snapshot(
                    snapshot.totalProducts(),
                    snapshot.brandsAnalyzed(),
                    snapshot.topBrands() == null ? List.of() : snapshot.topBrands(),
                    snapshot.topCategories() == null ? List.of() : snapshot.topCategories()
            );
        }
    }
}
