package com.undercontroll.application.usecase;

import com.undercontroll.application.dto.OrderEnrichedDto;
import com.undercontroll.application.mapper.OrderDtoMapper;
import com.undercontroll.domain.usecase.order.impl.GetOrdersImpl;
import com.undercontroll.domain.model.Order;
import com.undercontroll.domain.model.PaginatedResult;
import com.undercontroll.domain.enums.OrderStatus;
import com.undercontroll.domain.usecase.order.GetOrdersPort;
import com.undercontroll.domain.gateway.OrderGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetOrdersImplTest {

    @Mock
    private OrderGateway orderGateway;

    @Mock
    private OrderDtoMapper orderMapper;

    @InjectMocks
    private GetOrdersImpl getOrdersImpl;

    private Order order;

    private static final Integer DEFAULT_OFFSET = 0;
    private static final Integer DEFAULT_LIMIT  = 10;

    @BeforeEach
    void setUp() {
        order = Order.builder()
                .id(1)
                .status(OrderStatus.PENDING)
                .total(100.0)
                .discount(10.0)
                .orderItems(new ArrayList<>())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Should return all orders successfully")
    void testGetOrders_ShouldReturnAllOrders() {
        when(orderGateway.findAllPaginated(DEFAULT_OFFSET, DEFAULT_LIMIT)).thenReturn(new PaginatedResult<>(List.of(order), 1L));
        when(orderMapper.toEnrichedDto(order)).thenReturn(
                new OrderEnrichedDto(1, null, null, null, null, null, null, null, null, null, null, false, null, null, OrderStatus.PENDING, null));

        GetOrdersPort.Output output = getOrdersImpl.execute(new GetOrdersPort.Input(DEFAULT_OFFSET, DEFAULT_LIMIT));

        assertNotNull(output);
        assertNotNull(output.orders());
        assertEquals(1, output.orders().size());

        OrderEnrichedDto dto = output.orders().get(0);
        assertEquals(1, dto.id());
        assertEquals(OrderStatus.PENDING, dto.status());
        assertEquals(1L, output.totalElements());
        assertEquals(1, output.totalPages());

        verify(orderGateway, times(1)).findAllPaginated(DEFAULT_OFFSET, DEFAULT_LIMIT);
        verify(orderGateway, never()).findAll();
    }

    @Test
    @DisplayName("Should return empty list when no orders exist")
    void testGetOrders_ShouldReturnEmptyList_WhenNoOrders() {
        when(orderGateway.findAllPaginated(DEFAULT_OFFSET, DEFAULT_LIMIT)).thenReturn(new PaginatedResult<>(List.of(), 0L));

        GetOrdersPort.Output output = getOrdersImpl.execute(new GetOrdersPort.Input(DEFAULT_OFFSET, DEFAULT_LIMIT));

        assertNotNull(output);
        assertTrue(output.orders().isEmpty());

        verify(orderGateway, times(1)).findAllPaginated(DEFAULT_OFFSET, DEFAULT_LIMIT);
        verify(orderGateway, never()).findAll();
    }

    @Test
    @DisplayName("Should forward offset and limit to gateway correctly")
    void testGetOrders_ShouldForwardOffsetAndLimitToGateway() {
        Integer offset = 5;
        Integer limit  = 3;

        when(orderGateway.findAllPaginated(offset, limit)).thenReturn(new PaginatedResult<>(List.of(), 0L));

        getOrdersImpl.execute(new GetOrdersPort.Input(offset, limit));

        verify(orderGateway, times(1)).findAllPaginated(offset, limit);
    }

    @Test
    @DisplayName("Should map each order to enriched DTO")
    void testGetOrders_ShouldMapEachOrderToEnrichedDto() {
        Order order2 = Order.builder()
                .id(2)
                .status(OrderStatus.COMPLETED)
                .total(200.0)
                .discount(0.0)
                .orderItems(new ArrayList<>())
                .updatedAt(LocalDateTime.now())
                .build();

        OrderEnrichedDto dto1 = new OrderEnrichedDto(1, null, null, null, null, null, null, null, null, null, null, false, null, null, OrderStatus.PENDING, null);
        OrderEnrichedDto dto2 = new OrderEnrichedDto(2, null, null, null, null, null, null, null, null, null, null, false, null, null, OrderStatus.COMPLETED, null);

        when(orderGateway.findAllPaginated(DEFAULT_OFFSET, DEFAULT_LIMIT)).thenReturn(new PaginatedResult<>(List.of(order, order2), 2L));
        when(orderMapper.toEnrichedDto(order)).thenReturn(dto1);
        when(orderMapper.toEnrichedDto(order2)).thenReturn(dto2);

        GetOrdersPort.Output output = getOrdersImpl.execute(new GetOrdersPort.Input(DEFAULT_OFFSET, DEFAULT_LIMIT));

        assertEquals(2, output.orders().size());
        assertEquals(OrderStatus.PENDING, output.orders().get(0).status());
        assertEquals(OrderStatus.COMPLETED, output.orders().get(1).status());

        verify(orderMapper, times(1)).toEnrichedDto(order);
        verify(orderMapper, times(1)).toEnrichedDto(order2);
    }
}
