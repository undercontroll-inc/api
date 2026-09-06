package com.undercontroll.domain.market;

import com.undercontroll.domain.model.market.MarketProductCurrent;
import com.undercontroll.domain.model.market.MatchCoverage;
import com.undercontroll.domain.model.market.RepairCatalogItem;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class RepairCatalogMatcher {

    private RepairCatalogMatcher() {
    }

    public static MatchCoverage coverage(List<MarketProductCurrent> products, List<RepairCatalogItem> catalog) {
        if (products == null || products.isEmpty()) {
            return MatchCoverage.empty();
        }
        CatalogIndex index = CatalogIndex.from(catalog);
        int exact = 0;
        int brandCategory = 0;
        int category = 0;
        int none = 0;
        for (MarketProductCurrent product : products) {
            switch (index.matchLevel(product)) {
                case EXACT -> exact++;
                case BRAND_CATEGORY -> brandCategory++;
                case CATEGORY -> category++;
                default -> none++;
            }
        }
        return new MatchCoverage(exact, brandCategory, category, none);
    }

    public static Set<String> clientDomainIds(List<RepairCatalogItem> catalog) {
        Set<String> domains = new HashSet<>();
        if (catalog == null) {
            return domains;
        }
        for (RepairCatalogItem item : catalog) {
            if (item.domainId() != null) {
                domains.add(item.domainId());
            }
        }
        return domains;
    }

    private enum MatchLevel {
        EXACT,
        BRAND_CATEGORY,
        CATEGORY,
        NONE
    }

    private record CatalogIndex(Set<String> productKeys, Set<String> brandDomains, Set<String> domains) {

        static CatalogIndex from(List<RepairCatalogItem> catalog) {
            Set<String> productKeys = new HashSet<>();
            Set<String> brandDomains = new HashSet<>();
            Set<String> domains = new HashSet<>();
            if (catalog == null) {
                return new CatalogIndex(productKeys, brandDomains, domains);
            }
            for (RepairCatalogItem item : catalog) {
                if (item.productKey() != null) {
                    productKeys.add(item.productKey());
                }
                if (item.brandSlug() != null && item.domainId() != null) {
                    brandDomains.add(item.brandSlug() + "|" + item.domainId());
                }
                if (item.domainId() != null) {
                    domains.add(item.domainId());
                }
            }
            return new CatalogIndex(productKeys, brandDomains, domains);
        }

        MatchLevel matchLevel(MarketProductCurrent product) {
            if (product.productKey() != null && productKeys.contains(product.productKey())) {
                return MatchLevel.EXACT;
            }
            if (product.brandSlug() != null
                    && product.domainId() != null
                    && brandDomains.contains(product.brandSlug() + "|" + product.domainId())) {
                return MatchLevel.BRAND_CATEGORY;
            }
            if (product.domainId() != null && domains.contains(product.domainId())) {
                return MatchLevel.CATEGORY;
            }
            return MatchLevel.NONE;
        }
    }
}
