package com.undercontroll.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.undercontroll.api.config.SecurityConfig;
import com.undercontroll.api.dto.*;
import com.undercontroll.api.model.Order;
import com.undercontroll.api.model.enums.OrderStatus;
import com.undercontroll.api.model.enums.UserType;
import com.undercontroll.api.service.OrderService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Import(SecurityConfig.class)
@AutoConfigureMockMvc(addFilters = true)
@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private OrderService orderService;

    @Test
    @WithMockUser(authorities = "SCOPE_ADMINISTRATOR")
    @DisplayName("POST /v1/api/orders - ADMINISTRATOR should create order and return 201")
    void administratorShouldCreateOrderSuccessfully() throws Exception {
        CreateOrderRequest request = new CreateOrderRequest(
                1, List.of(), List.of(), 0.0, "20/11/2025", "25/11/2025",
                "Service description", "Notes", "PENDING", false, true, "NF123"
        );

        Order order = Order.builder()
                .id(1)
                .status(OrderStatus.PENDING)
                .build();

        when(orderService.createOrder(any(CreateOrderRequest.class))).thenReturn(order);

        mockMvc.perform(post("/v1/api/orders")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("PENDING"));

        verify(orderService, times(1)).createOrder(any(CreateOrderRequest.class));
    }

    @Test
    @WithMockUser(authorities = "SCOPE_CUSTOMER")
    @DisplayName("POST /v1/api/orders - CUSTOMER should be forbidden and return 403")
    void customerShouldBeForbiddenToCreateOrder() throws Exception {
        CreateOrderRequest request = new CreateOrderRequest(
                1, List.of(), List.of(), 0.0, "20/11/2025", "25/11/2025",
                "Service description", "Notes", "PENDING", false, true, "NF123"
        );

        mockMvc.perform(post("/v1/api/orders")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());

        verify(orderService, never()).createOrder(any(CreateOrderRequest.class));
    }

    @Test
    @WithMockUser(authorities = "SCOPE_ADMINISTRATOR")
    @DisplayName("PATCH /v1/api/orders/{id} - ADMINISTRATOR should update order and return 200")
    void administratorShouldUpdateOrderSuccessfully() throws Exception {
        UpdateOrderRequest request = new UpdateOrderRequest(
                OrderStatus.COMPLETED, List.of(), List.of(), "Updated description"
        );

        doNothing().when(orderService).updateOrder(any(UpdateOrderRequest.class), eq(1));

        mockMvc.perform(patch("/v1/api/orders/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(orderService, times(1)).updateOrder(any(UpdateOrderRequest.class), eq(1));
    }

    @Test
    @WithMockUser(authorities = "SCOPE_ADMINISTRATOR")
    @DisplayName("GET /v1/api/orders - ADMINISTRATOR should get all orders and return 200")
    void administratorShouldGetAllOrdersSuccessfully() throws Exception {
        UserDto userDto = new UserDto(1, "John", "john@example.com", "Doe",
                "Street 123", "12345678900", "12345-678", "11999999999",
                null, false, false, true, UserType.CUSTOMER);

        OrderEnrichedDto order = new OrderEnrichedDto(
                1, userDto, List.of(), List.of(), 100.0, 50.0, 10.0, 140.0,
                "20/11/2025", "25/11/2025", "NF123", true, "Description",
                null, OrderStatus.PENDING, "20/11/2025"
        );

        GetAllOrdersResponse response = new GetAllOrdersResponse(List.of(order));

        when(orderService.getOrders()).thenReturn(response);

        mockMvc.perform(get("/v1/api/orders")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].status").value("PENDING"));

        verify(orderService, times(1)).getOrders();
    }

    @Test
    @WithMockUser(authorities = "SCOPE_CUSTOMER")
    @DisplayName("GET /v1/api/orders - CUSTOMER should be forbidden and return 403")
    void customerShouldBeForbiddenToGetAllOrders() throws Exception {
        mockMvc.perform(get("/v1/api/orders")
                        .with(csrf()))
                .andExpect(status().isForbidden());

        verify(orderService, never()).getOrders();
    }

    @Test
    @WithMockUser(authorities = "SCOPE_CUSTOMER")
    @DisplayName("GET /v1/api/orders/{orderId} - CUSTOMER should get order by id and return 200")
    void customerShouldGetOrderByIdSuccessfully() throws Exception {
        UserDto userDto = new UserDto(1, "John", "john@example.com", "Doe",
                "Street 123", "12345678900", "12345-678", "11999999999",
                null, false, false, true, UserType.CUSTOMER);

        OrderEnrichedDto order = new OrderEnrichedDto(
                1, userDto, List.of(), List.of(), 100.0, 50.0, 10.0, 140.0,
                "20/11/2025", "25/11/2025", "NF123", true, "Description",
                null, OrderStatus.PENDING, "20/11/2025"
        );

        GetOrderByIdResponse response = new GetOrderByIdResponse(order);

        when(orderService.getOrderById(eq(1), anyString())).thenReturn(response);

        mockMvc.perform(get("/v1/api/orders/1")
                        .with(csrf())
                        .header("Authorization", "Bearer test-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.status").value("PENDING"));

        verify(orderService, times(1)).getOrderById(eq(1), anyString());
    }

    @Test
    @WithMockUser(authorities = "SCOPE_ADMINISTRATOR")
    @DisplayName("DELETE /v1/api/orders/{orderId} - ADMINISTRATOR should delete order and return 204")
    void administratorShouldDeleteOrderSuccessfully() throws Exception {
        doNothing().when(orderService).deleteOrder(1);

        mockMvc.perform(delete("/v1/api/orders/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(orderService, times(1)).deleteOrder(1);
    }

    @Test
    @WithMockUser(authorities = "SCOPE_CUSTOMER")
    @DisplayName("DELETE /v1/api/orders/{orderId} - CUSTOMER should be forbidden and return 403")
    void customerShouldBeForbiddenToDeleteOrder() throws Exception {
        mockMvc.perform(delete("/v1/api/orders/1")
                        .with(csrf()))
                .andExpect(status().isForbidden());

        verify(orderService, never()).deleteOrder(anyInt());
    }

    @Test
    @WithMockUser(authorities = "SCOPE_CUSTOMER")
    @DisplayName("GET /v1/api/orders/filter?userId=X - CUSTOMER should get orders by userId and return 200")
    void customerShouldGetOrdersByUserIdSuccessfully() throws Exception {
        UserDto userDto = new UserDto(1, "John", "john@example.com", "Doe",
                "Street 123", "12345678900", "12345-678", "11999999999",
                null, false, false, true, UserType.CUSTOMER);

        OrderEnrichedDto order = new OrderEnrichedDto(
                1, userDto, List.of(), List.of(), 100.0, 50.0, 10.0, 140.0,
                "20/11/2025", "25/11/2025", "NF123", true, "Description",
                null, OrderStatus.PENDING, "20/11/2025"
        );

        GetOrdersByUserIdResponse response = new GetOrdersByUserIdResponse(List.of(order));

        when(orderService.getOrdersByUserId(1)).thenReturn(response);

        mockMvc.perform(get("/v1/api/orders/filter")
                        .with(csrf())
                        .param("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].user.id").value(1));

        verify(orderService, times(1)).getOrdersByUserId(1);
    }

    @Test
    @WithMockUser(authorities = "SCOPE_ADMINISTRATOR")
    @DisplayName("GET /v1/api/orders/filter?userId=X - ADMINISTRATOR should get orders by userId and return 200")
    void administratorShouldGetOrdersByUserIdSuccessfully() throws Exception {
        UserDto userDto = new UserDto(1, "John", "john@example.com", "Doe",
                "Street 123", "12345678900", "12345-678", "11999999999",
                null, false, false, true, UserType.CUSTOMER);

        OrderEnrichedDto order = new OrderEnrichedDto(
                1, userDto, List.of(), List.of(), 100.0, 50.0, 10.0, 140.0,
                "20/11/2025", "25/11/2025", "NF123", true, "Description",
                null, OrderStatus.PENDING, "20/11/2025"
        );

        GetOrdersByUserIdResponse response = new GetOrdersByUserIdResponse(List.of(order));

        when(orderService.getOrdersByUserId(1)).thenReturn(response);

        mockMvc.perform(get("/v1/api/orders/filter")
                        .with(csrf())
                        .param("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].id").value(1));

        verify(orderService, times(1)).getOrdersByUserId(1);
    }
}
