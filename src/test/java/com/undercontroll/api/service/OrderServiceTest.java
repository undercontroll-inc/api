package com.undercontroll.api.service;

import com.undercontroll.api.dto.*;
import com.undercontroll.api.exception.*;
import com.undercontroll.api.model.*;
import com.undercontroll.api.model.enums.OrderStatus;
import com.undercontroll.api.model.enums.UserType;
import com.undercontroll.api.repository.OrderJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do Serviço de Pedidos")
class OrderServiceTest {

    @Mock
    private OrderJpaRepository repository;

    @Mock
    private OrderItemService orderItemService;

    @Mock
    private DemandService demandService;

    @Mock
    private UserService userService;

    @Mock
    private InventoryManagementService inventoryManagementService;

    @InjectMocks
    private OrderService orderService;

    private User user;
    private ComponentPart component;
    private Order order;
    private OrderItem orderItem;
    private Demand demand;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1);
        user.setName("João Silva");
        user.setUserType(UserType.CUSTOMER);

        component = new ComponentPart();
        component.setId(1);
        component.setName("Resistor 10K");
        component.setQuantity(100L);
        component.setPrice(0.50);

        orderItem = new OrderItem();
        orderItem.setId(1);
        orderItem.setBrand("Samsung");
        orderItem.setModel("Galaxy A50");
        orderItem.setType("Smartphone");
        orderItem.setLaborValue(50.0);

        demand = new Demand();
        demand.setId(1);
        demand.setComponent(component);
        demand.setQuantity(10L);

        order = Order.builder()
                .id(1)
                .user(user)
                .orderItems(new ArrayList<>(List.of(orderItem)))
                .status(OrderStatus.PENDING)
                .discount(0.0)
                .total(55.0)
                .nf("NF-123")
                .fabricGuarantee(true)
                .returnGuarantee(true)
                .description("Troca de tela")
                .received_at(LocalDate.now())
                .completedTime(LocalDate.now().plusDays(7))
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Deve criar um pedido com sucesso")
    void testCreateOrder_ShouldCreateSuccessfully() {
        CreateOrderRequest request = new CreateOrderRequest(
                1,
                List.of(new OrderItemCreateOrderRequest("Electrolux", "MEF41", "Microondas",
                        "Magnetron queimado", "220V", "SN987654", 80.0)),
                List.of(new PartDto(1, 10)),
                0.0,
                "15/01/2025",
                "22/01/2025",
                "Troca do magnetron",
                "Cliente relatou que não aquece mais",
                "PENDING",
                true,
                true,
                "NF-123"
        );

        when(inventoryManagementService.getComponentById(1)).thenReturn(component);
        when(userService.getUserById(1)).thenReturn(user);
        when(orderItemService.createOrderItem(any())).thenReturn(orderItem);
        when(repository.save(any(Order.class))).thenReturn(order);
        doNothing().when(inventoryManagementService).validateStockAvailability(any(), anyInt());
        doNothing().when(inventoryManagementService).decreaseStock(anyInt(), anyInt());
        when(demandService.createDemand(any())).thenReturn(demand);

        Order result = orderService.createOrder(request);

        assertNotNull(result);
        verify(inventoryManagementService, times(1)).validateStockAvailability(component, 10);
        verify(inventoryManagementService, times(1)).decreaseStock(1, 10);
        verify(repository, times(1)).save(any(Order.class));
        verify(demandService, times(1)).createDemand(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar pedido com estoque insuficiente")
    void testCreateOrder_ShouldThrowException_WhenInsufficientStock() {
        CreateOrderRequest request = new CreateOrderRequest(
                1,
                List.of(new OrderItemCreateOrderRequest("Arno", "LN27", "Liquidificador",
                        "Motor não funciona", "127V", "SN123456", 45.0)),
                List.of(new PartDto(1, 200)),
                0.0,
                "15/01/2025",
                "22/01/2025",
                "Troca do motor",
                "Cliente informou que fez barulho estranho antes de parar",
                "PENDING",
                true,
                true,
                "NF-123"
        );

        when(inventoryManagementService.getComponentById(1)).thenReturn(component);
        doThrow(new InsuficientComponentException("Insufficient stock"))
                .when(inventoryManagementService).validateStockAvailability(component, 200);

        assertThrows(InsuficientComponentException.class, () -> orderService.createOrder(request));
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Deve atualizar um pedido com sucesso")
    void testUpdateOrder_ShouldUpdateSuccessfully() {
        UpdateOrderRequest request = new UpdateOrderRequest(
                OrderStatus.COMPLETED,
                List.of(new UpdateOrderItemDto(1, "Smartphone", "Samsung", "Galaxy A50",
                        "220V", "SN123", 50.0, "Tela trocada")),
                List.of(new PartDto(1, 5)),
                "Serviço concluído"
        );

        when(repository.findById(1)).thenReturn(Optional.of(order));
        when(demandService.findDemandByOrderAndComponent(any(), anyInt())).thenReturn(Optional.of(demand));
        when(inventoryManagementService.getComponentById(1)).thenReturn(component);
        doNothing().when(inventoryManagementService).increaseStock(anyInt(), anyInt());
        when(demandService.updateDemand(any())).thenReturn(demand);
        when(repository.save(any(Order.class))).thenReturn(order);

        assertDoesNotThrow(() -> orderService.updateOrder(request, 1));

        verify(repository, times(1)).save(any(Order.class));
        verify(inventoryManagementService, times(1)).increaseStock(1, 5);
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar pedido inexistente")
    void testUpdateOrder_ShouldThrowException_WhenOrderNotFound() {
        UpdateOrderRequest request = new UpdateOrderRequest(
                OrderStatus.COMPLETED,
                List.of(),
                List.of(),
                "Descrição"
        );

        when(repository.findById(999)).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class, () -> orderService.updateOrder(request, 999));
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Deve buscar todos os pedidos")
    void testGetOrders_ShouldReturnAllOrders() {
        when(repository.findAll()).thenReturn(List.of(order));
        when(repository.calculatePartsTotalByOrderId(anyInt())).thenReturn(5.0);
        when(userService.mapToDto(any(User.class))).thenReturn(
                new UserDto(1, "João Silva", "joao@email.com", "Silva", "Rua A",
                        "123456789", "12345-678", "999999999", null, true, false, false, UserType.CUSTOMER));
        when(orderItemService.mapToDto(any(OrderItem.class))).thenReturn(
                new OrderItemDto(1, null, "Galaxy A50", "Smartphone", "Samsung",
                        "Tela quebrada", "220V", "SN123", 50.0, null));

        GetAllOrdersResponse response = orderService.getOrders();

        assertNotNull(response);
        assertFalse(response.data().isEmpty());
        verify(repository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve buscar pedido por ID com sucesso")
    void testGetOrderById_ShouldReturnOrder_WhenExists() {
        when(repository.findById(1)).thenReturn(Optional.of(order));
        when(userService.getUserByToken(anyString())).thenReturn(user);
        when(repository.calculatePartsTotalByOrderId(anyInt())).thenReturn(5.0);
        when(userService.mapToDto(any(User.class))).thenReturn(
                new UserDto(1, "João Silva", "joao@email.com", "Silva", "Rua A",
                        "123456789", "12345-678", "999999999", null, true, false,false, UserType.CUSTOMER));
        when(orderItemService.mapToDto(any(OrderItem.class))).thenReturn(
                new OrderItemDto(1, null, "Galaxy A50", "Smartphone", "Samsung",
                        "Tela quebrada", "220V", "SN123", 50.0, null));

        GetOrderByIdResponse response = orderService.getOrderById(1, "token");

        assertNotNull(response);
        assertEquals(1, response.data().id());
        verify(repository, times(1)).findById(1);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar pedido com ID inválido")
    void testGetOrderById_ShouldThrowException_WhenInvalidId() {
        assertThrows(InvalidUpdateOrderException.class,
                () -> orderService.getOrderById(null, "token"));
        assertThrows(InvalidUpdateOrderException.class,
                () -> orderService.getOrderById(-1, "token"));
    }

    @Test
    @DisplayName("Deve lançar exceção quando usuário não autorizado tenta acessar pedido")
    void testGetOrderById_ShouldThrowException_WhenUnauthorizedUser() {
        User otherUser = new User();
        otherUser.setId(2);
        otherUser.setUserType(UserType.CUSTOMER);

        when(repository.findById(1)).thenReturn(Optional.of(order));
        when(userService.getUserByToken(anyString())).thenReturn(otherUser);

        assertThrows(UnauthorizedOrderOperation.class,
                () -> orderService.getOrderById(1, "token"));
    }

    @Test
    @DisplayName("Deve buscar pedidos por ID do usuário")
    void testGetOrdersByUserId_ShouldReturnUserOrders() {
        when(repository.findByUser_id(1)).thenReturn(List.of(order));
        when(repository.calculatePartsTotalByOrderId(anyInt())).thenReturn(5.0);
        when(userService.mapToDto(any(User.class))).thenReturn(
                new UserDto(1, "João Silva", "joao@email.com", "Silva", "Rua A",
                        "123456789", "12345-678", "999999999", null, true, false,false, UserType.CUSTOMER));
        when(orderItemService.mapToDto(any(OrderItem.class))).thenReturn(
                new OrderItemDto(1, null, "Galaxy A50", "Smartphone", "Samsung",
                        "Tela quebrada", "220V", "SN123", 50.0, null));

        GetOrdersByUserIdResponse response = orderService.getOrdersByUserId(1);

        assertNotNull(response);
        assertFalse(response.data().isEmpty());
        verify(repository, times(1)).findByUser_id(1);
    }

    @Test
    @DisplayName("Deve deletar um pedido com sucesso")
    void testDeleteOrder_ShouldDeleteSuccessfully() {
        when(repository.findById(1)).thenReturn(Optional.of(order));
        doNothing().when(repository).delete(any(Order.class));

        assertDoesNotThrow(() -> orderService.deleteOrder(1));

        verify(repository, times(1)).delete(order);
    }

    @Test
    @DisplayName("Deve lançar exceção ao deletar pedido com ID inválido")
    void testDeleteOrder_ShouldThrowException_WhenInvalidId() {
        assertThrows(InvalidDeleteOrderException.class,
                () -> orderService.deleteOrder(null));
        assertThrows(InvalidDeleteOrderException.class,
                () -> orderService.deleteOrder(-1));
    }

    @Test
    @DisplayName("Deve lançar exceção ao deletar pedido inexistente")
    void testDeleteOrder_ShouldThrowException_WhenOrderNotFound() {
        when(repository.findById(999)).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class, () -> orderService.deleteOrder(999));
        verify(repository, never()).delete(any());
    }

    @Test
    @DisplayName("Deve remover demanda quando quantidade for zero")
    void testUpdateOrder_ShouldRemoveDemand_WhenQuantityIsZero() {
        UpdateOrderRequest request = new UpdateOrderRequest(
                null,
                List.of(),
                List.of(new PartDto(1, 0)),
                null
        );

        when(repository.findById(1)).thenReturn(Optional.of(order));
        when(demandService.findDemandByOrderAndComponent(any(), anyInt())).thenReturn(Optional.of(demand));
        when(inventoryManagementService.getComponentById(1)).thenReturn(component);
        doNothing().when(inventoryManagementService).increaseStock(anyInt(), anyInt());
        doNothing().when(demandService).deleteDemand(any());
        when(repository.save(any(Order.class))).thenReturn(order);

        assertDoesNotThrow(() -> orderService.updateOrder(request, 1));

        verify(inventoryManagementService, times(1)).increaseStock(1, 10);
        verify(demandService, times(1)).deleteDemand(demand);
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar pedido com data inválida")
    void testCreateOrder_ShouldThrowException_WhenInvalidDateFormat() {
        CreateOrderRequest request = new CreateOrderRequest(
                1,
                List.of(),
                List.of(new PartDto(1, 10)),
                0.0,
                "data-invalida",
                "22/01/2025",
                "Descrição",
                "Observações",
                "PENDING",
                true,
                true,
                "NF-123"
        );

        assertThrows(InvalidOrderDateException.class, () -> orderService.createOrder(request));
    }

    @Test
    @DisplayName("Deve buscar componentes de um pedido")
    void testGetPartsByOrderId_ShouldReturnComponents() {
        List<Object[]> mockResults = new ArrayList<>();
        // Order: id, name, description, brand, price, supplier, category, quantity
        mockResults.add(new Object[]{1, "Resistor 10K", "Descrição", "Marca", 0.50, "Fornecedor", "Categoria", 10L});

        when(repository.findAllPartsByOrderIdNative(1)).thenReturn(mockResults);

        List<ComponentDto> components = orderService.getPartsByOrderId(1);

        assertNotNull(components);
        assertEquals(1, components.size());
        assertEquals("Resistor 10K", components.get(0).item());
        verify(repository, times(1)).findAllPartsByOrderIdNative(1);
    }

    @Test
    @DisplayName("Deve calcular total do pedido corretamente incluindo peças e mão de obra")
    void testCreateOrder_ShouldCalculateTotalCorrectly() {
        CreateOrderRequest request = new CreateOrderRequest(
                1,
                List.of(new OrderItemCreateOrderRequest("Samsung", "Galaxy A50", "Smartphone",
                        "Tela quebrada", "220V", "SN123", 50.0)),
                List.of(new PartDto(1, 10)),
                5.0,
                "15/01/2025",
                "22/01/2025",
                "Troca de tela",
                "Observações",
                "PENDING",
                true,
                true,
                "NF-123"
        );

        when(inventoryManagementService.getComponentById(1)).thenReturn(component);
        when(userService.getUserById(1)).thenReturn(user);
        when(orderItemService.createOrderItem(any())).thenReturn(orderItem);
        when(repository.save(any(Order.class))).thenAnswer(invocation -> {
            Order savedOrder = invocation.getArgument(0);
            assertEquals(50.0, savedOrder.getTotal());
            return savedOrder;
        });
        doNothing().when(inventoryManagementService).validateStockAvailability(any(), anyInt());
        doNothing().when(inventoryManagementService).decreaseStock(anyInt(), anyInt());
        when(demandService.createDemand(any())).thenReturn(demand);

        orderService.createOrder(request);

        verify(repository, times(1)).save(any(Order.class));
    }

    @Test
    @DisplayName("Deve aumentar estoque quando quantidade de demanda diminui")
    void testUpdateOrder_ShouldIncreaseStock_WhenDemandQuantityDecreases() {
        UpdateOrderRequest request = new UpdateOrderRequest(
                null,
                List.of(),
                List.of(new PartDto(1, 5)),
                null
        );

        when(repository.findById(1)).thenReturn(Optional.of(order));
        when(demandService.findDemandByOrderAndComponent(any(), anyInt())).thenReturn(Optional.of(demand));
        when(inventoryManagementService.getComponentById(1)).thenReturn(component);
        doNothing().when(inventoryManagementService).increaseStock(anyInt(), anyInt());
        when(demandService.updateDemand(any())).thenReturn(demand);
        when(repository.save(any(Order.class))).thenReturn(order);

        assertDoesNotThrow(() -> orderService.updateOrder(request, 1));

        verify(inventoryManagementService, times(1)).increaseStock(1, 5);
        verify(demandService, times(1)).updateDemand(any());
    }

    @Test
    @DisplayName("Deve diminuir estoque quando quantidade de demanda aumenta")
    void testUpdateOrder_ShouldDecreaseStock_WhenDemandQuantityIncreases() {
        UpdateOrderRequest request = new UpdateOrderRequest(
                null,
                List.of(),
                List.of(new PartDto(1, 15)),
                null
        );

        when(repository.findById(1)).thenReturn(Optional.of(order));
        when(demandService.findDemandByOrderAndComponent(any(), anyInt())).thenReturn(Optional.of(demand));
        when(inventoryManagementService.getComponentById(1)).thenReturn(component);
        doNothing().when(inventoryManagementService).validateStockAvailability(any(), anyInt());
        doNothing().when(inventoryManagementService).decreaseStock(anyInt(), anyInt());
        when(demandService.updateDemand(any())).thenReturn(demand);
        when(repository.save(any(Order.class))).thenReturn(order);

        assertDoesNotThrow(() -> orderService.updateOrder(request, 1));

        verify(inventoryManagementService, times(1)).validateStockAvailability(component, 5);
        verify(inventoryManagementService, times(1)).decreaseStock(1, 5);
        verify(demandService, times(1)).updateDemand(any());
    }

    @Test
    @DisplayName("Deve permitir que ADMIN acesse qualquer pedido")
    void testGetOrderById_ShouldAllow_WhenUserIsAdmin() {
        User adminUser = new User();
        adminUser.setId(2);
        adminUser.setUserType(UserType.ADMINISTRATOR);

        when(repository.findById(1)).thenReturn(Optional.of(order));
        when(userService.getUserByToken(anyString())).thenReturn(adminUser);
        when(repository.calculatePartsTotalByOrderId(anyInt())).thenReturn(5.0);
        when(userService.mapToDto(any(User.class))).thenReturn(
                new UserDto(1, "João Silva", "joao@email.com", "Silva", "Rua A",
                        "123456789", "12345-678", "999999999", null, true, false,false, UserType.CUSTOMER));
        when(orderItemService.mapToDto(any(OrderItem.class))).thenReturn(
                new OrderItemDto(1, null, "Galaxy A50", "Smartphone", "Samsung",
                        "Tela quebrada", "220V", "SN123", 50.0, null));

        assertDoesNotThrow(() -> orderService.getOrderById(1, "token"));

        verify(repository, times(1)).findById(1);
    }
}

