package com.undercontroll.domain.model.market;

public record RepairMixItem(
        String domainId,
        String type,
        long volume,
        double sharePct
) {
}
