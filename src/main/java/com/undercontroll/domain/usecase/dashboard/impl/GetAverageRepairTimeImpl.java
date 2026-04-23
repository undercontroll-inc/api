package com.undercontroll.domain.usecase.dashboard.impl;

import com.undercontroll.application.dto.DashboardMetricsResponse;
import com.undercontroll.domain.enums.OrderStatus;
import com.undercontroll.domain.gateway.OrderGateway;
import com.undercontroll.domain.usecase.dashboard.DashboardDateFilter;
import com.undercontroll.domain.usecase.dashboard.GetAverageRepairTimePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GetAverageRepairTimeImpl implements GetAverageRepairTimePort {

    private final OrderGateway orderGateway;

    @Override
    public Output execute(Input input) {
        LocalDate startDate = DashboardDateFilter.from(input.period());
        List<String> statuses = input.status().getStatuses().stream()
                .map(OrderStatus::name)
                .toList();

        Double avgHours = orderGateway.calculateAverageRepairTimeFiltered(startDate, statuses);

        return new Output(new DashboardMetricsResponse(avgHours));
    }
}
