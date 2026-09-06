package com.undercontroll.domain.usecase.dashboard;

import com.undercontroll.application.dto.dashboard.DashboardMetricsResponse;
import com.undercontroll.domain.enums.PeriodFilter;
import com.undercontroll.domain.enums.StatusFilter;

public interface GetTotalRevenuePort {
    DashboardMetricsResponse execute(PeriodFilter period, StatusFilter status);
}
