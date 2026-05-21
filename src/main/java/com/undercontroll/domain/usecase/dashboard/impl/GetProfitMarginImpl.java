package com.undercontroll.domain.usecase.dashboard.impl;

import com.undercontroll.domain.usecase.dashboard.DashboardDateFilter;
import com.undercontroll.domain.usecase.dashboard.GetProfitMarginPort;
import com.undercontroll.domain.gateway.OrderGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class GetProfitMarginImpl implements GetProfitMarginPort {

    private final OrderGateway orderGateway;

    @Override
    @Cacheable(value = "dashboardMetrics", key = "#input.period().toString() + '-' + #input.status().toString() + '-profitMargin'")
    public Output execute(Input input) {
        LocalDate startDate = DashboardDateFilter.from(input.period());
        var statuses = input.status().getStatuses().stream().map(Enum::name).toList();

        Double totalRevenue = orderGateway.calculateTotalRevenueFiltered(startDate, statuses);
        Double totalPartsCost = orderGateway.calculateTotalPartsCostFiltered(startDate, statuses);
        Double profitMargin = totalRevenue - totalPartsCost;

        return new Output(profitMargin);
    }
}
