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

    public InsightGrounding grounding(InsightGenerationContext.State state) {
        InsightsPayload.CoberturaMatch coverage = matchCoverage(state);
        List<RepairCatalogLine> catalog = repairCatalog(state);
        List<RepairMixItem> mix = repairMix(state);
        MarketSnapshot snapshot = marketSnapshot(state);
        return new InsightGrounding(coverage, catalog, mix, InsightGrounding.Snapshot.from(snapshot));
    }

    InsightsPayload.CoberturaMatch matchCoverage(InsightGenerationContext.State state) {
        InsightsPayload.CoberturaMatch coverage = InsightsPayload.CoberturaMatch.from(state.coverage());
        state.evidence().ingest(coverage);
        return coverage;
    }

    List<RepairCatalogLine> repairCatalog(InsightGenerationContext.State state) {
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

    List<RepairMixItem> repairMix(InsightGenerationContext.State state) {
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

    MarketSnapshot marketSnapshot(InsightGenerationContext.State state) {
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

    @Tool(name = "list_price_movements", description = """
            Altas e quedas de preço por categoria. Já filtrado: histórico presente e product_count >= 2.
            Use para ALERTA_PRECO. Não use para cobrir catálogo, cobertura ou snapshot — isso já está no contexto.
            """)
    public List<MarketPriceMovement> listPriceMovements(ToolContext toolContext) {
        InsightGenerationContext.State state = InsightGenerationContext.require(toolContext);
        List<MarketPriceMovement> rows = marketViewGateway.findPriceMovements(state.bucketKey());
        state.evidence().ingest(rows);
        return rows;
    }

    @Tool(name = "list_stock_opportunities", description = """
            Oportunidade de estoque: preço caindo com volume de ofertas subindo.
            Use para OPORTUNIDADE_ESTOQUE.
            """)
    public List<MarketPriceMovement> listStockOpportunities(ToolContext toolContext) {
        InsightGenerationContext.State state = InsightGenerationContext.require(toolContext);
        List<MarketPriceMovement> rows = marketViewGateway.findStockOpportunities(state.bucketKey());
        state.evidence().ingest(rows);
        return rows;
    }

    @Tool(name = "list_rising_products", description = """
            Produtos em alta com confidence HIGH. Não descreva subida para produtos LOW.
            Use para TENDENCIA_ALTA.
            """)
    public List<MarketRisingProduct> listRisingProducts(ToolContext toolContext) {
        InsightGenerationContext.State state = InsightGenerationContext.require(toolContext);
        List<MarketRisingProduct> rows = marketViewGateway.findRisingProductsHighConfidence();
        state.evidence().ingest(rows);
        return rows;
    }

    @Tool(name = "list_brand_momentum", description = """
            Resumo de marcas no mês corrente: presença no ranking, rising_count e faixa de preço.
            Use para DESTAQUE_MARCA.
            """)
    public List<MarketBrandSummary> listBrandMomentum(ToolContext toolContext) {
        InsightGenerationContext.State state = InsightGenerationContext.require(toolContext);
        List<MarketBrandSummary> rows = marketViewGateway.findBrandMomentum(state.bucketKey());
        state.evidence().ingest(rows);
        return rows;
    }

    @Tool(name = "list_category_summary", description = """
            Resumo por tipo de aparelho (domain_id) no mês corrente. Mais completo que o top 5 do snapshot.
            Use para ALERTA_MERCADO ou para cruzar mix de consertos com o mercado.
            """)
    public List<MarketCategorySummary> listCategorySummary(ToolContext toolContext) {
        InsightGenerationContext.State state = InsightGenerationContext.require(toolContext);
        List<MarketCategorySummary> rows = marketViewGateway.findCategorySummary(state.bucketKey());
        state.evidence().ingest(rows);
        return rows;
    }

    @Tool(name = "list_price_dispersion", description = """
            Dispersão de preço de produtos PRODUCT com offer_count >= 3.
            Use para ALERTA_MERCADO quando o ponto for faixa de preço, não ranking.
            """)
    public List<MarketProductCurrent> listPriceDispersion(ToolContext toolContext) {
        InsightGenerationContext.State state = InsightGenerationContext.require(toolContext);
        List<MarketProductCurrent> rows = marketViewGateway.findPriceDispersion();
        state.evidence().ingest(rows);
        return rows;
    }

    @Tool(name = "list_uncovered_categories", description = """
            Categorias em alta que a assistência ainda não atende (sem match de domain_id no catálogo).
            Use para OPORTUNIDADE_REPARO. O catálogo já está no contexto.
            """)
    public List<MarketCategorySummary> listUncoveredCategories(ToolContext toolContext) {
        InsightGenerationContext.State state = InsightGenerationContext.require(toolContext);
        List<MarketCategorySummary> rows = marketViewGateway.findUncoveredCategories(
                state.bucketKey(),
                state.clientDomainIds());
        state.evidence().ingest(rows);
        return rows;
    }
}
