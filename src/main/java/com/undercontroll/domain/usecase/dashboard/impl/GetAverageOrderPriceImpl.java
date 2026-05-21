package com.undercontroll.domain.usecase.dashboard.impl;

import com.undercontroll.domain.usecase.dashboard.DashboardDateFilter;
import com.undercontroll.domain.usecase.dashboard.GetAverageOrderPricePort;
import com.undercontroll.domain.gateway.OrderGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class GetAverageOrderPriceImpl implements GetAverageOrderPricePort {

    private final OrderGateway orderGateway;

    @Override
    @Cacheable(value = "dashboardMetrics", key = "#input.period().toString() + '-' + #input.status().toString() + '-averageOrderPrice'")
    public Output execute(Input input) {
        LocalDate startDate = DashboardDateFilter.from(input.period());
        var statusStrings = input.status().getStatuses().stream()
                .map(Enum::name)
                .toList();

        Double averagePrice = orderGateway.calculateAverageOrderPriceFiltered(startDate, statusStrings);

        return new Output(averagePrice);
    }
}
