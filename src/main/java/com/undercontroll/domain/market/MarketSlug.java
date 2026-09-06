package com.undercontroll.domain.market;

import java.text.Normalizer;
import java.util.regex.Pattern;

public final class MarketSlug {

    public static final int MAX_SLUG_LENGTH = 120;
    public static final int MAX_PRODUCT_KEY_LENGTH = 180;

    private static final Pattern SLUG_STRIP = Pattern.compile("[^a-z0-9]+");
    private static final Pattern COMBINING_MARKS = Pattern.compile("\\p{M}+");

    private MarketSlug() {
    }

    public static String slugify(String value) {
        return slugify(value, MAX_SLUG_LENGTH);
    }

    public static String slugify(String value, int maxLength) {
        if (value == null || value.isBlank()) {
            return null;
        }
        String decomposed = Normalizer.normalize(value.toLowerCase(), Normalizer.Form.NFKD);
        String withoutAccents = COMBINING_MARKS.matcher(decomposed).replaceAll("");
        String slug = SLUG_STRIP.matcher(withoutAccents).replaceAll("-")
                .replaceAll("^-+", "")
                .replaceAll("-+$", "");
        if (slug.isEmpty()) {
            return null;
        }
        return slug.length() > maxLength ? slug.substring(0, maxLength) : slug;
    }

    public static String productKey(String brand, String model) {
        String brandSlug = slugify(brand);
        String modelSlug = slugify(model);
        if (brandSlug == null || modelSlug == null) {
            return null;
        }
        String key = brandSlug + ":" + modelSlug;
        return key.length() > MAX_PRODUCT_KEY_LENGTH ? key.substring(0, MAX_PRODUCT_KEY_LENGTH) : key;
    }
}
