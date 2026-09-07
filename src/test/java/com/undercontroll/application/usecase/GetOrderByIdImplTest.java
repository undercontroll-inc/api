package com.undercontroll.application.usecase;

import com.undercontroll.application.dto.order.GetOrderByIdResponse;
import com.undercontroll.application.dto.order.OrderEnrichedDto;
import com.undercontroll.application.mapper.OrderDtoMapper;
import com.undercontroll.domain.gateway.CurrentUserAdminPort;
import com.undercontroll.domain.usecase.order.impl.GetOrderByIdImpl;
import com.undercontroll.domain.model.Order;
import com.undercontroll.domain.enums.OrderStatus;
import com.undercontroll.domain.gateway.OrderGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetOrderByIdImplTest {

    @Mock
    private OrderGateway orderGateway;

    @Mock
    private OrderDtoMapper orderMapper;

    @Mock
    private CurrentUserAdminPort currentUserAdminPort;

    @InjectMocks
    private GetOrderByIdImpl getOrderByIdImpl;

    private Order order;

    @BeforeEach
    void setUp() {
        order = Order.builder()
                .id(1)
                .status(OrderStatus.PENDING)
                .total(100.0)
                .discount(0.0)
                .customerDescription("Não gela")
                .technicalDescription("Compressor queimado")
                .nf("NF123")
                .returnGuarantee(true)
                .build();
    }

    @Test
    @DisplayName("Should return order when it exists")
    void testGetOrderById_ShouldReturnOrder_WhenExists() {
        when(orderGateway.findById(1)).thenReturn(Optional.of(order));
        when(orderMapper.toEnrichedDto(order, false)).thenReturn(
                new OrderEnrichedDto(1, null, null, null, null, null, null, null, null, null, null, false, "Não gela", null, OrderStatus.PENDING, null));

        Optional<GetOrderByIdResponse> output = getOrderByIdImpl.execute(1);

        assertTrue(output.isPresent());
        assertNotNull(output.get().data());
        assertEquals(1, output.get().data().id());
        assertEquals(OrderStatus.PENDING, output.get().data().status());
        assertEquals("Não gela", output.get().data().customerDescription());
        assertNull(output.get().data().technicalDescription());

        verify(orderGateway, times(1)).findById(1);
        verify(orderMapper).toEnrichedDto(order, false);
    }

    @Test
    @DisplayName("Administrator mapping includes the technical description flag")
    void administratorMapsWithTechnicalDescription() {
        when(currentUserAdminPort.isAdministrator()).thenReturn(true);
        when(orderGateway.findById(1)).thenReturn(Optional.of(order));
        when(orderMapper.toEnrichedDto(order, true)).thenReturn(
                new OrderEnrichedDto(1, null, null, null, null, null, null, null, null, null, null, false, "Não gela", "Compressor queimado", OrderStatus.PENDING, null));

        Optional<GetOrderByIdResponse> output = getOrderByIdImpl.execute(1);

        assertTrue(output.isPresent());
        assertEquals("Compressor queimado", output.get().data().technicalDescription());
        verify(orderMapper).toEnrichedDto(order, true);
    }

    @Test
    @DisplayName("Should return empty when order does not exist")
    void testGetOrderById_ShouldReturnNullOrder_WhenNotFound() {
        when(orderGateway.findById(999)).thenReturn(Optional.empty());

        Optional<GetOrderByIdResponse> output = getOrderByIdImpl.execute(999);

        assertTrue(output.isEmpty());

        verify(orderGateway, times(1)).findById(999);
    }
}
