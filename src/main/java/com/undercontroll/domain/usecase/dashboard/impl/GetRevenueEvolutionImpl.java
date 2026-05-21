package com.undercontroll.domain.usecase.dashboard.impl;

import com.undercontroll.application.dto.RevenueEvolutionResponse;
import com.undercontroll.domain.enums.OrderStatus;
import com.undercontroll.domain.gateway.OrderGateway;
import com.undercontroll.domain.usecase.dashboard.DashboardDateFilter;
import com.undercontroll.domain.usecase.dashboard.GetRevenueEvolutionPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GetRevenueEvolutionImpl implements GetRevenueEvolutionPort {

    private final OrderGateway orderGateway;

    @Override
    public Output execute(Input input) {
        LocalDate startDate = DashboardDateFilter.from(input.period());
        List<String> statuses = input.status().getStatuses().stream()
                .map(OrderStatus::name)
                .toList();

        List<Object[]> rows = orderGateway.getRevenueEvolution(startDate, statuses);

        List<RevenueEvolutionResponse.DataPoint> dataPoints = rows.stream()
                .map(row -> new RevenueEvolutionResponse.DataPoint(
                        toLocalDate(row[0]),
                        toDouble(row[1]),
                        toDouble(row[2]),
                        toLong(row[3])
                ))
                .toList();

        return new Output(new RevenueEvolutionResponse(dataPoints));
    }

    private LocalDate toLocalDate(Object value) {
        if (value == null) return null;
        if (value instanceof Date sqlDate) return sqlDate.toLocalDate();
        if (value instanceof LocalDate ld) return ld;
        return null;
    }

    private Double toDouble(Object value) {
        if (value == null) return 0.0;
        if (value instanceof BigDecimal bd) return bd.doubleValue();
        if (value instanceof Double d) return d;
        if (value instanceof Number n) return n.doubleValue();
        return 0.0;
    }

    private Long toLong(Object value) {
        if (value == null) return 0L;
        if (value instanceof Long l) return l;
        if (value instanceof Number n) return n.longValue();
        return 0L;
    }
}
