package com.undercontroll.domain.model.market;

public record RepairCatalogItem(
        String brand,
        String model,
        String type,
        long volume,
        String brandSlug,
        String productKey,
        String domainId
) {
}
