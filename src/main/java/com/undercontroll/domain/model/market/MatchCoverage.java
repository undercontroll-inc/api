package com.undercontroll.domain.model.market;

public record MatchCoverage(
        int exact,
        int brandCategory,
        int category,
        int none
) {
    public static MatchCoverage empty() {
        return new MatchCoverage(0, 0, 0, 0);
    }
}
