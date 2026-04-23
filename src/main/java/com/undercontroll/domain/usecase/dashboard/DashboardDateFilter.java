package com.undercontroll.domain.usecase.dashboard;

import com.undercontroll.domain.enums.PeriodFilter;

import java.time.LocalDate;

public final class DashboardDateFilter {

    private DashboardDateFilter() {}

    public static LocalDate from(PeriodFilter period) {
        if (period == null || period == PeriodFilter.ALL) return null;
        return period.getDays() != null ? LocalDate.now().minusDays(period.getDays()) : null;
    }
}
