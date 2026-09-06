package com.undercontroll.infrastructure.ai;

import com.undercontroll.domain.gateway.MarketViewGateway;
import com.undercontroll.domain.model.insight.InsightsPayload;
import com.undercontroll.domain.model.market.MarketBrandSummary;
import com.undercontroll.domain.model.market.MarketCategorySummary;
import com.undercontroll.domain.model.market.MarketPriceMovement;
import com.undercontroll.domain.model.market.MarketProductCurrent;
import com.undercontroll.domain.model.market.MarketRisingProduct;
import com.undercontroll.domain.model.market.MarketSnapshot;
import com.undercontroll.domain.model.market.RepairCatalogItem;
import com.undercontroll.domain.model.market.RepairCatalogLine;
import com.undercontroll.domain.model.market.RepairMixItem;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MarketInsightTools {

    private static final int REPAIR_CATALOG_LIMIT = 25;
    private static final int SNAPSHOT_TOP_N = 5;

    private final MarketViewGateway marketViewGateway;

    @Tool(description = "Cobertura do cruzamento entre produtos do Mercado Livre e o catálogo de consertos. Copie estes números; não recalcule.")
    public InsightsPayload.CoberturaMatch getMatchCoverage(ToolContext toolContext) {
        InsightsPayload.CoberturaMatch coverage = InsightsPayload.CoberturaMatch.from(
                InsightGenerationContext.require(toolContext).coverage());
        InsightGenerationContext.require(toolContext).evidence().ingest(coverage);
        return coverage;
    }

    @Tool(description = "Catálogo de consertos dos últimos 90 dias: até 25 linhas (marca, modelo, tipo CRM, domain_id, volume), ordenadas por volume.")
    public List<RepairCatalogLine> getRepairCatalog(ToolContext toolContext) {
        InsightGenerationContext.State state = InsightGenerationContext.require(toolContext);
        List<RepairCatalogLine> rows = state.catalog().stream()
                .sorted(Comparator.comparingLong(RepairCatalogItem::volume).reversed())
                .limit(REPAIR_CATALOG_LIMIT)
                .map(item -> new RepairCatalogLine(
                        item.brand(),
                        item.model(),
                        item.type(),
                        item.domainId(),
                        item.volume()
                ))
                .toList();
        state.evidence().ingest(rows);
        return rows;
    }

    @Tool(description = "Mix do catálogo de consertos agregado por domain_id e tipo CRM: volume e share percentual. Cruze com list_category_summary.")
    public List<RepairMixItem> getRepairMix(ToolContext toolContext) {
        InsightGenerationContext.State state = InsightGenerationContext.require(toolContext);
        List<RepairCatalogItem> catalog = state.catalog();
        long total = catalog.stream().mapToLong(RepairCatalogItem::volume).sum();
        record MixKey(String domainId, String type) {
        }
        Map<MixKey, Long> volumes = catalog.stream().collect(Collectors.groupingBy(
                item -> new MixKey(item.domainId(), item.type()),
                Collectors.summingLong(RepairCatalogItem::volume)
        ));
        List<RepairMixItem> rows = volumes.entrySet().stream()
                .sorted(Map.Entry.<MixKey, Long>comparingByValue().reversed())
                .map(entry -> new RepairMixItem(
                        entry.getKey().domainId(),
                        entry.getKey().type(),
                        entry.getValue(),
                        total == 0 ? 0.0 : Math.round(entry.getValue() * 1000.0 / total) / 10.0
                ))
                .toList();
        state.evidence().ingest(rows);
        return rows;
    }

    @Tool(description = "Snapshot do mercado no bucket corrente: totais, top 5 marcas e top 5 categorias. Mesmos KPIs do GET /analytics.")
    public MarketSnapshot getMarketSnapshot(ToolContext toolContext) {
        InsightGenerationContext.State state = InsightGenerationContext.require(toolContext);
        String bucketKey = state.bucketKey();
        List<MarketSnapshot.BrandLine> brands = marketViewGateway.findTopBrands(bucketKey, SNAPSHOT_TOP_N).stream()
                .map(row -> new MarketSnapshot.BrandLine(
                        row.brandName(),
                        row.brandSlug(),
                        row.productCount() == null ? 0L : row.productCount(),
                        row.bestRank(),
                        row.avgScore()
                ))
                .toList();
        List<MarketSnapshot.CategoryLine> categories = marketViewGateway.findTopCategories(bucketKey, SNAPSHOT_TOP_N).stream()
                .map(row -> new MarketSnapshot.CategoryLine(
                        row.domainId(),
                        row.categoryName(),
                        row.productCount() == null ? 0L : row.productCount(),
                        row.avgScore(),
                        row.risingCount()
                ))
                .toList();
        MarketSnapshot snapshot = new MarketSnapshot(
                bucketKey,
                marketViewGateway.countCurrentProducts(),
                marketViewGateway.countDistinctBrands(),
                brands,
                categories
        );
        state.evidence().ingest(snapshot);
        return snapshot;
    }

    @Tool(description = "Altas e quedas de preço por categoria. Já filtrado: histórico presente e product_count >= 2.")
    public List<MarketPriceMovement> listPriceMovements(ToolContext toolContext) {
        InsightGenerationContext.State state = InsightGenerationContext.require(toolContext);
        List<MarketPriceMovement> rows = marketViewGateway.findPriceMovements(state.bucketKey());
        state.evidence().ingest(rows);
        return rows;
    }

    @Tool(description = "Oportunidade de estoque: preço caindo com volume de ofertas subindo.")
    public List<MarketPriceMovement> listStockOpportunities(ToolContext toolContext) {
        InsightGenerationContext.State state = InsightGenerationContext.require(toolContext);
        List<MarketPriceMovement> rows = marketViewGateway.findStockOpportunities(state.bucketKey());
        state.evidence().ingest(rows);
        return rows;
    }

    @Tool(description = "Produtos em alta com confidence HIGH. Não descreva subida para produtos LOW.")
    public List<MarketRisingProduct> listRisingProducts(ToolContext toolContext) {
        InsightGenerationContext.State state = InsightGenerationContext.require(toolContext);
        List<MarketRisingProduct> rows = marketViewGateway.findRisingProductsHighConfidence();
        state.evidence().ingest(rows);
        return rows;
    }

    @Tool(description = "Resumo de marcas no bucket corrente: presença no ranking, rising_count e faixa de preço.")
    public List<MarketBrandSummary> listBrandMomentum(ToolContext toolContext) {
        InsightGenerationContext.State state = InsightGenerationContext.require(toolContext);
        List<MarketBrandSummary> rows = marketViewGateway.findBrandMomentum(state.bucketKey());
        state.evidence().ingest(rows);
        return rows;
    }

    @Tool(description = "Resumo por tipo de aparelho (domain_id) no bucket corrente.")
    public List<MarketCategorySummary> listCategorySummary(ToolContext toolContext) {
        InsightGenerationContext.State state = InsightGenerationContext.require(toolContext);
        List<MarketCategorySummary> rows = marketViewGateway.findCategorySummary(state.bucketKey());
        state.evidence().ingest(rows);
        return rows;
    }

    @Tool(description = "Dispersão de preço de produtos PRODUCT com offer_count >= 3.")
    public List<MarketProductCurrent> listPriceDispersion(ToolContext toolContext) {
        InsightGenerationContext.State state = InsightGenerationContext.require(toolContext);
        List<MarketProductCurrent> rows = marketViewGateway.findPriceDispersion();
        state.evidence().ingest(rows);
        return rows;
    }

    @Tool(description = "Categorias em alta que o cliente ainda não atende (sem match de domain_id no catálogo de consertos).")
    public List<MarketCategorySummary> listUncoveredCategories(ToolContext toolContext) {
        InsightGenerationContext.State state = InsightGenerationContext.require(toolContext);
        List<MarketCategorySummary> rows = marketViewGateway.findUncoveredCategories(
                state.bucketKey(),
                state.clientDomainIds());
        state.evidence().ingest(rows);
        return rows;
    }
}
