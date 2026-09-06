package com.undercontroll.domain.market;

import com.undercontroll.domain.model.market.MarketCategorySummary;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public final class ApplianceTypeDomainMap {

    private static final String DOMAIN_MICROWAVES = "MLB-MICROWAVES";
    private static final String DOMAIN_AIR_FRYERS = "MLB-AIR_FRYERS";
    private static final Map<String, String> TYPE_TO_DOMAIN = new LinkedHashMap<>();

    static {
        put("microondas", DOMAIN_MICROWAVES);
        put("micro-ondas", DOMAIN_MICROWAVES);
        put("microwave", DOMAIN_MICROWAVES);
        put("airfryer", DOMAIN_AIR_FRYERS);
        put("air-fryer", DOMAIN_AIR_FRYERS);
        put("fritadeira", DOMAIN_AIR_FRYERS);
        put("liquidificador", "MLB-BLENDERS");
        put("blender", "MLB-BLENDERS");
        put("cafeteira", "MLB-COFFEE_MAKERS");
        put("expresso", "MLB-ESPRESSO_MACHINES");
        put("espresso", "MLB-ESPRESSO_MACHINES");
        put("batedeira", "MLB-STAND_MIXERS");
        put("mixer", "MLB-HAND_BLENDERS");
        put("processador", "MLB-FOOD_PROCESSORS");
        put("multiprocessador", "MLB-FOOD_PROCESSORS");
        put("espremedor", "MLB-ELECTRIC_SQUEEZERS");
        put("sanduicheira", "MLB-ELECTRIC_SANDWICH_MAKERS");
        put("grill", "MLB-ELECTRIC_GRILLS");
        put("torradeira", "MLB-TOASTERS");
        put("chaleira", "MLB-ELECTRIC_KETTLES");
        put("forno", "MLB-ELECTRIC_OVENS");
        put("cooktop", "MLB-COOKTOPS");
        put("ventilador", "MLB-FANS");
        put("circulador", "MLB-FANS");
        put("purificador", "MLB-WATER_PURIFIERS");
        put("umidificador", "MLB-HUMIDIFIERS");
        put("aspirador", "MLB-VACUUM_CLEANERS");
        put("ferro", "MLB-IRONS");
        put("secador", "MLB-HAIR_DRYERS");
        put("chapinha", "MLB-HAIR_STRAIGHTENERS");
        put("prancha", "MLB-HAIR_STRAIGHTENERS");
        put("pipoqueira", "MLB-POPCORN_MACHINES");
        put("waffle", "MLB-WAFFLE_MAKERS");
        put("iogurteira", "MLB-YOGURT_MAKERS");
        put("panela", "MLB-ELECTRIC_PRESSURE_COOKERS");
        put("maquina-de-pao", "MLB-BREAD_MAKERS");
    }

    private ApplianceTypeDomainMap() {
    }

    public static Set<String> typeSlugs() {
        return Set.copyOf(TYPE_TO_DOMAIN.keySet());
    }

    public static String resolve(String type, Collection<MarketCategorySummary> categories) {
        String slug = MarketSlug.slugify(type);
        if (slug == null) {
            return null;
        }
        String mapped = TYPE_TO_DOMAIN.get(slug);
        if (mapped != null) {
            return mapped;
        }
        if (categories == null) {
            return null;
        }
        for (MarketCategorySummary category : categories) {
            String nameSlug = MarketSlug.slugify(category.categoryName());
            String domainSlug = MarketSlug.slugify(category.domainId());
            if ((nameSlug != null && (nameSlug.contains(slug) || slug.contains(nameSlug)))
                    || (domainSlug != null && domainSlug.contains(slug))) {
                return category.domainId();
            }
        }
        return null;
    }

    private static void put(String typeSlug, String domainId) {
        TYPE_TO_DOMAIN.put(typeSlug, domainId);
    }
}
