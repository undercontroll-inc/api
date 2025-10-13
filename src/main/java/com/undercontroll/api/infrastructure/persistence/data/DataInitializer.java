package com.undercontroll.api.infrastructure.persistence.data;

import com.undercontroll.api.domain.enums.UserType;
import com.undercontroll.api.domain.model.Order;
import com.undercontroll.api.domain.model.ServiceOrder;
import com.undercontroll.api.domain.model.User;
import com.undercontroll.api.infrastructure.persistence.repository.OrderJpaRepository;
import com.undercontroll.api.infrastructure.persistence.repository.ServiceOrderJpaRepository;
import com.undercontroll.api.infrastructure.persistence.repository.UserJpaRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.ArrayList;

@Component
public class DataInitializer {

    @Autowired
    private UserJpaRepository userRepository;

    @Autowired
    private OrderJpaRepository orderRepository;

    @Autowired
    private ServiceOrderJpaRepository serviceOrderRepository;

    @Autowired
    private BCryptPasswordEncoder encoder;

    @PostConstruct
    public void init() {

//        User user = User.builder()
//                .name("Lucas Furquim")
//                .lastName("Furquim")
//                .email("lucas@gmail.com")
//                .password(encoder.encode("123"))
//                .address("Rua")
//                .cpf("55739713870")
//                .birthDate(Date.valueOf("2006-03-26"))
//                .userType(UserType.ADMINISTRATOR)
//                .build();
//
//        user = userRepository.save(user);
//
//        Order order = Order.builder()
//                .startedAt(LocalDateTime.now())
//                .createdAt(LocalDateTime.now())
//                .orderItems(new ArrayList<>())
//                .build();
//
//        order = orderRepository.save(order);
//
//        ServiceOrder serviceOrder = ServiceOrder.builder()
//                .user(user)
//                .order(order)
//                .fabricGuarantee(12)
//                .budget(15000)
//                .returnGuarantee(30)
//                .description("Notebook Dell Inspiron 15 3000")
//                .nf("NF-2024-001234")
//                .date(Date.valueOf("2024-10-11"))
//                .store("Loja TechMundo - Shopping Center")
//                .issue("Tela com pixels mortos na região superior direita")
//                .componentPartList(new ArrayList<>())
//                .build();
//
//        serviceOrderRepository.save(serviceOrder);

    }
}