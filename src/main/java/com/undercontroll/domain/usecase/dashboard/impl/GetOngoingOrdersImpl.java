package com.undercontroll.domain.usecase.dashboard.impl;

import com.undercontroll.domain.usecase.dashboard.GetOngoingOrdersPort;
import com.undercontroll.domain.enums.PeriodFilter;
import com.undercontroll.domain.gateway.OrderGateway;
import com.undercontroll.application.dto.DashboardMetricsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class GetOngoingOrdersImpl implements GetOngoingOrdersPort {

    private final OrderGateway orderGateway;

    @Override
    public Output execute(Input input) {
        LocalDate startDate = getStartDate(input.period());
        Long count = orderGateway.countOngoingOrdersFiltered(startDate, null);
        return new Output(new DashboardMetricsResponse(count.doubleValue()));
    }

    private LocalDate getStartDate(PeriodFilter period) {
        if (period == null || period == PeriodFilter.ALL) {
            return LocalDate.now().minusYears(10);
        }
        Integer days = period.getDays();
        return LocalDate.now().minusDays(days);
    }
}
