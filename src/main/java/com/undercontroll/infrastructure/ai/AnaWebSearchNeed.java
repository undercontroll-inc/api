package com.undercontroll.infrastructure.ai;

import java.util.Locale;
import java.util.regex.Pattern;

public final class AnaWebSearchNeed {

    private static final Pattern NEED = Pattern.compile(
            "\\bmanual\\b|\\brecall\\b|\\bdica\\b|\\binternet\\b|\\bweb\\b",
            Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE
    );

    private AnaWebSearchNeed() {
    }

    public static boolean matches(String text) {
        return text != null && NEED.matcher(text.toLowerCase(Locale.ROOT)).find();
    }
}
