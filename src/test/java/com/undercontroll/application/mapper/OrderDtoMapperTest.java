package com.undercontroll.application.mapper;

import com.undercontroll.application.dto.order.OrderEnrichedDto;
import com.undercontroll.domain.enums.OrderStatus;
import com.undercontroll.domain.model.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderDtoMapperTest {

    @Mock
    private UserDtoMapper userDtoMapper;

    @Mock
    private OrderItemDtoMapper orderItemDtoMapper;

    @Mock
    private ComponentPartDtoMapper componentDtoMapper;

    @InjectMocks
    private OrderDtoMapper mapper;

    private Order order;

    @BeforeEach
    void setUp() {
        order = Order.builder()
                .id(7)
                .status(OrderStatus.PENDING)
                .discount(0.0)
                .total(80.0)
                .customerDescription("Não gela")
                .technicalDescription("Compressor queimado")
                .nf("NF-1")
                .returnGuarantee(true)
                .build();
        when(userDtoMapper.toDto(null)).thenReturn(null);
    }

    @Test
    @DisplayName("includes technicalDescription when the caller is admin")
    void includesTechnicalWhenRequested() {
        OrderEnrichedDto dto = mapper.toEnrichedDto(order, true);

        assertEquals("Não gela", dto.customerDescription());
        assertEquals("Compressor queimado", dto.technicalDescription());
    }

    @Test
    @DisplayName("hides technicalDescription for a customer caller")
    void hidesTechnicalWhenNotRequested() {
        OrderEnrichedDto dto = mapper.toEnrichedDto(order, false);

        assertEquals("Não gela", dto.customerDescription());
        assertNull(dto.technicalDescription());
    }
}
