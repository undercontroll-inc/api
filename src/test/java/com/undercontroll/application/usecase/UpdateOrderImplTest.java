package com.undercontroll.application.usecase;

import com.undercontroll.application.dto.order.UpdateOrderRequest;
import com.undercontroll.domain.usecase.order.impl.UpdateOrderImpl;
import com.undercontroll.domain.exception.InvalidUpdateOrderException;
import com.undercontroll.domain.exception.OrderNotFoundException;
import com.undercontroll.domain.model.Order;
import com.undercontroll.domain.enums.OrderStatus;
import com.undercontroll.infrastructure.service.MetricsService;
import com.undercontroll.domain.gateway.OrderGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateOrderImplTest {

    @Mock
    private OrderGateway orderGateway;

    @Mock
    private MetricsService metricsService;

    @InjectMocks
    private UpdateOrderImpl updateOrderImpl;

    private Order existingOrder;

    @BeforeEach
    void setUp() {
        existingOrder = Order.builder()
                .id(1)
                .status(OrderStatus.PENDING)
                .total(100.0)
                .discount(0.0)
                .description("Original description")
                .build();
    }

    @Test
    @DisplayName("Should update order status successfully")
    void testUpdateOrder_ShouldUpdateSuccessfully() {
        when(orderGateway.findById(1)).thenReturn(Optional.of(existingOrder));
        when(orderGateway.save(any(Order.class))).thenReturn(existingOrder);

        UpdateOrderRequest request = new UpdateOrderRequest(
                OrderStatus.IN_ANALYSIS, List.of(), List.of(), "Updated description"
        );

        updateOrderImpl.execute(1, request);

        verify(orderGateway, times(1)).findById(1);
        verify(orderGateway, times(1)).save(any(Order.class));
    }

    @Test
    @DisplayName("Should increment completed metric when status is COMPLETED")
    void testUpdateOrder_ShouldIncrementCompletedMetric_WhenStatusIsCompleted() {
        when(orderGateway.findById(1)).thenReturn(Optional.of(existingOrder));
        when(orderGateway.save(any(Order.class))).thenReturn(existingOrder);
        doNothing().when(metricsService).incrementOrderCompleted();

        UpdateOrderRequest request = new UpdateOrderRequest(
                OrderStatus.COMPLETED, List.of(), List.of(), null
        );

        updateOrderImpl.execute(1, request);

        verify(metricsService, times(1)).incrementOrderCompleted();
    }

    @Test
    @DisplayName("Should throw OrderNotFoundException when order not found")
    void testUpdateOrder_ShouldThrowException_WhenOrderNotFound() {
        when(orderGateway.findById(999)).thenReturn(Optional.empty());
        doNothing().when(metricsService).incrementOrderUpdateFailed();

        UpdateOrderRequest request = new UpdateOrderRequest(
                OrderStatus.COMPLETED, List.of(), List.of(), null
        );

        assertThrows(OrderNotFoundException.class, () -> updateOrderImpl.execute(999, request));

        verify(orderGateway, times(1)).findById(999);
        verify(orderGateway, never()).save(any());
        verify(metricsService, times(1)).incrementOrderUpdateFailed();
    }

    @Test
    @DisplayName("Should throw InvalidUpdateOrderException when orderId is null")
    void testUpdateOrder_ShouldThrowException_WhenOrderIdIsNull() {
        UpdateOrderRequest request = new UpdateOrderRequest(
                OrderStatus.COMPLETED, List.of(), List.of(), null
        );

        assertThrows(InvalidUpdateOrderException.class, () -> updateOrderImpl.execute(null, request));

        verify(orderGateway, never()).findById(any());
    }

    @Test
    @DisplayName("Should throw InvalidUpdateOrderException when orderId is zero or negative")
    void testUpdateOrder_ShouldThrowException_WhenOrderIdIsInvalid() {
        UpdateOrderRequest request = new UpdateOrderRequest(
                OrderStatus.COMPLETED, List.of(), List.of(), null
        );

        assertThrows(InvalidUpdateOrderException.class, () -> updateOrderImpl.execute(0, request));

        verify(orderGateway, never()).findById(any());
    }

    @Test
    @DisplayName("Should not change status when input status is null")
    void testUpdateOrder_WithNullStatus_ShouldNotChangeStatus() {
        when(orderGateway.findById(1)).thenReturn(Optional.of(existingOrder));
        when(orderGateway.save(any(Order.class))).thenReturn(existingOrder);

        UpdateOrderRequest request = new UpdateOrderRequest(
                null, List.of(), List.of(), "New description"
        );

        updateOrderImpl.execute(1, request);

        assertEquals(OrderStatus.PENDING, existingOrder.getStatus());
        verify(metricsService, never()).incrementOrderCompleted();
        verify(orderGateway, times(1)).save(existingOrder);
    }
}
