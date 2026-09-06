package com.undercontroll.domain.market;

import com.undercontroll.domain.model.market.MarketCategorySummary;
import com.undercontroll.domain.model.market.RepairCatalogItem;

import java.util.ArrayList;
import java.util.List;

public final class RepairCatalogFactory {

    private RepairCatalogFactory() {
    }

    public static List<RepairCatalogItem> fromRows(List<Object[]> rows, List<MarketCategorySummary> categories) {
        if (rows == null || rows.isEmpty()) {
            return List.of();
        }
        List<RepairCatalogItem> items = new ArrayList<>();
        for (Object[] row : rows) {
            String brand = asString(row, 0);
            String model = asString(row, 1);
            String type = asString(row, 2);
            long volume = asLong(row, 3);
            items.add(new RepairCatalogItem(
                    brand,
                    model,
                    type,
                    volume,
                    MarketSlug.slugify(brand),
                    MarketSlug.productKey(brand, model),
                    ApplianceTypeDomainMap.resolve(type, categories)
            ));
        }
        return items;
    }

    private static String asString(Object[] row, int index) {
        if (row == null || index >= row.length || row[index] == null) {
            return null;
        }
        return row[index].toString();
    }

    private static long asLong(Object[] row, int index) {
        if (row == null || index >= row.length || row[index] == null) {
            return 0L;
        }
        if (row[index] instanceof Number number) {
            return number.longValue();
        }
        return Long.parseLong(row[index].toString());
    }
}
