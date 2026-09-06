package com.undercontroll.domain.model.insight;

import java.time.YearMonth;
import java.time.format.DateTimeParseException;

public final class InsightMonthLabel {

    private static final String[] MONTHS = {
            "janeiro", "fevereiro", "março", "abril", "maio", "junho",
            "julho", "agosto", "setembro", "outubro", "novembro", "dezembro"
    };

    private InsightMonthLabel() {
    }

    public static String of(String bucketKey) {
        if (bucketKey == null || bucketKey.isBlank()) {
            return "mês indisponível";
        }
        try {
            YearMonth month = YearMonth.parse(bucketKey.trim());
            return "mês de " + MONTHS[month.getMonthValue() - 1] + " de " + month.getYear();
        } catch (DateTimeParseException ex) {
            return "mês indisponível";
        }
    }
}
