package com.undercontroll.domain.model.chat;

import java.util.List;

public record ShopSnapshot(
        List<RepairFact> openRepairs,
        List<RepairFact> readyForPickup,
        List<StockFact> lowStockParts,
        List<DemandFact> pendingParts,
        String lastAnnouncementTitle
) {

    public record RepairFact(Integer orderId, String customer, String appliance, String statusLabel) {
    }

    public record StockFact(String name, long quantity) {
    }

    public record DemandFact(String partName, Integer orderId, Long quantity) {
    }

    public static ShopSnapshot empty() {
        return new ShopSnapshot(List.of(), List.of(), List.of(), List.of(), null);
    }
}
