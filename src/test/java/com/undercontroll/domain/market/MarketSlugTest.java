package com.undercontroll.domain.market;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class MarketSlugTest {

    @Test
    @DisplayName("slugify matches ETL brand and model normalization")
    void slugifyMatchesEtl() {
        assertEquals("electrolux", MarketSlug.slugify("Electrolux"));
        assertEquals("me23s", MarketSlug.slugify("ME23S"));
        assertEquals("af-14", MarketSlug.slugify("AF-14"));
        assertEquals("micro-ondas", MarketSlug.slugify("Micro-ondas"));
        assertNull(MarketSlug.slugify(" "));
        assertNull(MarketSlug.slugify(null));
    }

    @Test
    @DisplayName("product_key is brand_slug:model_slug")
    void productKey() {
        assertEquals("electrolux:me23s", MarketSlug.productKey("Electrolux", "ME23S"));
        assertEquals("mondial:af-14", MarketSlug.productKey("Mondial", "AF-14"));
        assertNull(MarketSlug.productKey("Mondial", null));
    }
}
