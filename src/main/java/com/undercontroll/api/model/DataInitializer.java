package com.undercontroll.api.model;

import com.undercontroll.api.repository.OrderJpaRepository;
import com.undercontroll.api.repository.UserJpaRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer {

//    @Autowired
//    private UserJpaRepository userRepository;
//
//    @Autowired
//    private OrderJpaRepository orderRepository;
//
//    @Autowired
//    private BCryptPasswordEncoder encoder;
//
//    @PostConstruct
//    public void init() {

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

    //}
}