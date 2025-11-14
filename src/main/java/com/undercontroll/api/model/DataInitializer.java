package com.undercontroll.api.model;

import com.undercontroll.api.repository.ComponentJpaRepository;
import com.undercontroll.api.repository.DemandRepository;
import com.undercontroll.api.repository.OrderJpaRepository;
import com.undercontroll.api.repository.UserJpaRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
public class DataInitializer {

    @Autowired
    private UserJpaRepository userRepository;

    @Autowired
    private OrderJpaRepository orderRepository;

    @Autowired
    private ComponentJpaRepository componentRepository;

    @Autowired
    private DemandRepository demandRepository;

    @Autowired
    private PasswordEncoder encoder;

    @PostConstruct
    public void init() {

        // Criar ComponentPart
        ComponentPart component = ComponentPart.builder()
                .name("Peça")
                .brand("Marca")
                .description("Peça que ajuda em tal coisa")
                .price(190.0)
                .category("Peças de ferro")
                .supplier("Fornecedor")
                .quantity(12L)
                .build();
        component = componentRepository.save(component);

        // Criar User CUSTOMER
        User customer = User.builder()
                .name("Lucas Furquim")
                .email("lucas2@gmail.com")
                .lastName("Furquim")
                .password(encoder.encode("123"))
                .address("Rua")
                .cpf("55739713860")
                .CEP("09571300")
                .phone("11988310059")
                .userType(UserType.CUSTOMER)
                .build();
        customer = userRepository.save(customer);

        // Criar OrderItem (appliance)
        OrderItem orderItem = OrderItem.builder()
                .type("string")
                .brand("string")
                .model("string")
                .volt("string")
                .series("string")
                .observation("string")
                .laborValue(20.0)
                .build();

        String date = "12/12/2025";

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        // Criar Order
        Order order = Order.builder()
                .user(customer)
                .orderItems(new ArrayList<>(List.of(orderItem)))
                .demands(new ArrayList<>())
                .nf("string")
                .returnGuarantee(true)
                .fabricGuarantee(true)
                .discount(12.1)
                .received_at(LocalDate.parse(date, formatter))
                .description("string")
                .status(OrderStatus.PENDING)
                .build();
        order = orderRepository.save(order);

        // Criar Demand (parts)
        Demand demand = Demand.builder()
                .component(component)
                .order(order)
                .quantity(2L)
                .build();
        demandRepository.save(demand);

    }
}