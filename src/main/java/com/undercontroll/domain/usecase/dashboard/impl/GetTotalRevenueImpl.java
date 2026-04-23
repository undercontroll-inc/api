package com.undercontroll.domain.usecase.dashboard.impl;

import com.undercontroll.domain.usecase.dashboard.DashboardDateFilter;
import com.undercontroll.domain.usecase.dashboard.GetTotalRevenuePort;
import com.undercontroll.domain.gateway.OrderGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class GetTotalRevenueImpl implements GetTotalRevenuePort {

    private final OrderGateway orderGateway;

    @Override
    @Cacheable(value = "dashboardMetrics", key = "#input.period().toString() + '-' + #input.status().toString() + '-totalRevenue'")
    public Output execute(Input input) {
        LocalDate startDate = DashboardDateFilter.from(input.period());
        var statuses = input.status().getStatuses().stream().map(Enum::name).toList();

        Double totalRevenue = orderGateway.calculateTotalRevenueFiltered(startDate, statuses);

        return new Output(totalRevenue);
    }
}
