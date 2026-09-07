package com.undercontroll.infrastructure.ai;

import com.undercontroll.domain.gateway.MarketViewGateway;
import com.undercontroll.domain.model.insight.InsightPromptContext;
import com.undercontroll.domain.model.market.MarketBrandSummary;
import com.undercontroll.domain.model.market.MarketCategorySummary;
import com.undercontroll.domain.model.market.MatchCoverage;
import com.undercontroll.domain.model.market.RepairCatalogItem;
import com.undercontroll.domain.model.market.RepairCatalogLine;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.annotation.Tool;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MarketInsightToolsTest {

    @Mock
    private MarketViewGateway marketViewGateway;

    @InjectMocks
    private MarketInsightTools tools;

    @Test
    @DisplayName("grounding loads required facts and tools stay for optional deepening")
    void usesContext() {
        InsightPromptContext prompt = new InsightPromptContext(
                "2026-08",
                "2026-07",
                new MatchCoverage(2, 1, 0, 3),
                Set.of("MLB-MICROWAVES"),
                List.of(
                        new RepairCatalogItem("Philco", "PMO23", "MICROONDAS", 5, "philco", "pmo23", "MLB-MICROWAVES"),
                        new RepairCatalogItem("Electrolux", "ME23S", "MICROONDAS", 12, "electrolux", "me23s", "MLB-MICROWAVES"),
                        new RepairCatalogItem("Mondial", "AF-30", "AIRFRYER", 8, "mondial", "af-30", "MLB-AIR_FRYERS")
                )
        );
        EvidenceIndex evidence = new EvidenceIndex(new com.fasterxml.jackson.databind.ObjectMapper());
        InsightGenerationContext.State state = InsightGenerationContext.State.from(prompt, evidence);
        ToolContext toolContext = new ToolContext(Map.of(InsightGenerationContext.KEY, state));

        when(marketViewGateway.countCurrentProducts()).thenReturn(20L);
        when(marketViewGateway.countDistinctBrands()).thenReturn(8L);
        when(marketViewGateway.findTopBrands("2026-08", 5)).thenReturn(List.of(
                new MarketBrandSummary(
                        "electrolux", "2026-08", "Electrolux", 10L, 2L, 80.0, 1, 599.0,
                        400.0, 800.0, 2.0, 5.0, 3L)
        ));
        when(marketViewGateway.findTopCategories("2026-08", 5)).thenReturn(List.of(
                new MarketCategorySummary(
                        "MLB-MICROWAVES", "2026-08", "Micro-ondas", 12L, 4L, 70.0, 90.0,
                        599.0, 400.0, 800.0, 6.0, 1.0, 4.0, 2L, 1L, 2L)
        ));

        InsightGrounding grounding = tools.grounding(state);
        assertEquals(2, grounding.coberturaMatch().nivel1Exato());
        assertEquals(3, grounding.repairCatalog().size());
        assertEquals("Electrolux", grounding.repairCatalog().get(0).brand());
        assertEquals(12, grounding.repairCatalog().get(0).volume());
        assertEquals(2, grounding.repairMix().size());
        assertEquals("MLB-MICROWAVES", grounding.repairMix().get(0).domainId());
        assertEquals(17, grounding.repairMix().get(0).volume());
        assertEquals(68.0, grounding.repairMix().get(0).sharePct());
        assertEquals(20L, grounding.marketSnapshot().totalProducts());
        assertEquals(8L, grounding.marketSnapshot().brandsAnalyzed());
        assertEquals("Electrolux", grounding.marketSnapshot().topBrands().get(0).name());
        assertEquals("Micro-ondas", grounding.marketSnapshot().topCategories().get(0).name());
        assertTrue(evidence.contains(12));
        assertTrue(evidence.contains(20));

        when(marketViewGateway.findPriceMovements("2026-08")).thenReturn(List.of());
        when(marketViewGateway.findStockOpportunities("2026-08")).thenReturn(List.of());
        when(marketViewGateway.findRisingProductsHighConfidence()).thenReturn(List.of());
        when(marketViewGateway.findBrandMomentum("2026-08")).thenReturn(List.of());
        when(marketViewGateway.findCategorySummary("2026-08")).thenReturn(List.of());
        when(marketViewGateway.findPriceDispersion()).thenReturn(List.of());
        when(marketViewGateway.findUncoveredCategories("2026-08", Set.of("MLB-MICROWAVES"))).thenReturn(List.of());
        assertEquals(0, tools.listPriceMovements(toolContext).size());
        assertEquals(0, tools.listStockOpportunities(toolContext).size());
        assertEquals(0, tools.listRisingProducts(toolContext).size());
        assertEquals(0, tools.listBrandMomentum(toolContext).size());
        assertEquals(0, tools.listCategorySummary(toolContext).size());
        assertEquals(0, tools.listPriceDispersion(toolContext).size());
        assertEquals(0, tools.listUncoveredCategories(toolContext).size());
    }

    @Test
    @DisplayName("repair catalog grounding caps at 25 rows by volume")
    void capsRepairCatalog() {
        List<RepairCatalogItem> items = new ArrayList<>();
        for (int i = 1; i <= 30; i++) {
            items.add(new RepairCatalogItem(
                    "Marca" + i, "M" + i, "TIPO", i, "marca" + i, "m" + i, "MLB-X"));
        }
        InsightPromptContext prompt = new InsightPromptContext(
                "2026-08", "2026-07", MatchCoverage.empty(), Set.of(), items);
        InsightGenerationContext.State state = InsightGenerationContext.State.from(
                prompt, new EvidenceIndex(new com.fasterxml.jackson.databind.ObjectMapper()));

        List<RepairCatalogLine> catalog = tools.repairCatalog(state);
        assertEquals(25, catalog.size());
        assertEquals(30, catalog.get(0).volume());
        assertEquals(6, catalog.get(24).volume());
    }

    @Test
    @DisplayName("registers only optional deepening tools")
    void optionalToolsOnly() {
        Set<String> names = Arrays.stream(MarketInsightTools.class.getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(Tool.class))
                .map(method -> method.getAnnotation(Tool.class).name())
                .collect(Collectors.toSet());
        assertEquals(Set.of(
                "list_price_movements",
                "list_stock_opportunities",
                "list_rising_products",
                "list_brand_momentum",
                "list_category_summary",
                "list_price_dispersion",
                "list_uncovered_categories"
        ), names);
    }
}
