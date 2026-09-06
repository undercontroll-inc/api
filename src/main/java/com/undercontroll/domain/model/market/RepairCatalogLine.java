package com.undercontroll.domain.model.market;

public record RepairCatalogLine(
        String brand,
        String model,
        String type,
        String domainId,
        long volume
) {
}
