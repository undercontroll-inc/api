package com.undercontroll.infrastructure.ai;

import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Pattern;

public final class AnaShopToolNeed {

    private static final Pattern NEED = Pattern.compile(
            "\\bpecas?\\b|\\bestoque\\b|\\bcomponentes?\\b|\\bcategoria\\b|\\bfornecedor\\b|\\bavisos?\\b"
    );

    private AnaShopToolNeed() {
    }

    public static boolean matches(String text) {
        if (text == null || text.isBlank()) {
            return false;
        }
        String normalized = Normalizer.normalize(text.toLowerCase(Locale.ROOT), Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "");
        return NEED.matcher(normalized).find();
    }
}
