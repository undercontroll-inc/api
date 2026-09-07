package com.undercontroll.domain.usecase.order.impl;

import com.undercontroll.application.dto.order.GetAllOrdersResponse;
import com.undercontroll.application.mapper.OrderDtoMapper;
import com.undercontroll.domain.gateway.CurrentUserAdminPort;
import com.undercontroll.domain.model.PaginatedResult;
import com.undercontroll.domain.usecase.order.GetOrdersPort;
import com.undercontroll.domain.model.Order;
import com.undercontroll.domain.gateway.OrderGateway;
import com.undercontroll.application.dto.order.OrderEnrichedDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetOrdersImpl implements GetOrdersPort {

    private final OrderGateway orderGateway;
    private final OrderDtoMapper orderMapper;
    private final CurrentUserAdminPort currentUserAdminPort;

    @Override
    @Cacheable(value = "orders", key = "#userId + '-' + #page + '-' + #size + '-' + @securityContextCurrentUserId.isAdministrator()")
    public GetAllOrdersResponse execute(Integer userId, Integer page, Integer size) {
        boolean includeTechnical = currentUserAdminPort.isAdministrator();
        if (userId != null) {
            log.debug("Fetching orders for userId: {}", userId);

            List<OrderEnrichedDto> orders = orderGateway.findByUserId(userId).stream()
                    .map(order -> orderMapper.toEnrichedDto(order, includeTechnical))
                    .toList();

            return new GetAllOrdersResponse(orders, orders.size(), 1, 0, orders.size());
        }

        log.debug("Fetching all orders");

        PaginatedResult<Order> result = orderGateway.findAllPaginated(page, size);

        List<OrderEnrichedDto> enrichedOrders = result.content().stream()
                .map(order -> orderMapper.toEnrichedDto(order, includeTechnical))
                .toList();

        int totalPages = size > 0
                ? (int) Math.ceil((double) result.totalElements() / size)
                : 0;

        return new GetAllOrdersResponse(enrichedOrders, result.totalElements(), totalPages, page, size);
    }
}
