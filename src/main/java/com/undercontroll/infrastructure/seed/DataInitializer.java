package com.undercontroll.infrastructure.seed;

import com.undercontroll.domain.model.User;
import com.undercontroll.domain.model.ComponentPart;
import com.undercontroll.domain.model.Announcement;
import com.undercontroll.domain.model.Order;
import com.undercontroll.domain.model.OrderItem;
import com.undercontroll.domain.model.Demand;
import com.undercontroll.domain.enums.AnnouncementType;
import com.undercontroll.domain.enums.UserType;
import com.undercontroll.domain.enums.OrderStatus;
import com.undercontroll.infrastructure.mapper.UserMapper;
import com.undercontroll.infrastructure.mapper.ComponentPartMapper;
import com.undercontroll.infrastructure.mapper.AnnouncementMapper;
import com.undercontroll.infrastructure.mapper.OrderMapper;
import com.undercontroll.infrastructure.persistence.entity.UserJpaEntity;
import com.undercontroll.infrastructure.persistence.entity.ComponentPartJpaEntity;
import com.undercontroll.infrastructure.persistence.entity.AnnouncementJpaEntity;
import com.undercontroll.infrastructure.persistence.entity.OrderJpaEntity;
import com.undercontroll.infrastructure.persistence.repository.UserJpaRepository;
import com.undercontroll.infrastructure.persistence.repository.ComponentJpaRepository;
import com.undercontroll.infrastructure.persistence.repository.AnnouncementRepository;
import com.undercontroll.infrastructure.persistence.repository.OrderJpaRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer {

    private final UserJpaRepository userRepository;
    private final ComponentJpaRepository componentRepository;
    private final AnnouncementRepository announcementRepository;
    private final OrderJpaRepository orderRepository;
    private final PasswordEncoder encoder;
    private final UserMapper userMapper;
    private final ComponentPartMapper componentPartMapper;
    private final AnnouncementMapper announcementMapper;
    private final OrderMapper orderMapper;

    @PostConstruct
    public void init() {
        log.info("Initializing seed data...");
        
        if (userRepository.count() > 0) {
            log.info("Database already seeded, skipping...");
            return;
        }

        String defaultPassword = encoder.encode("123");

        User user1 = User.builder()
                .name("Admin")
                .lastName("Sistema")
                .email("admin@undercontroll.com")
                .password(defaultPassword)
                .address("Rua Principal, 100")
                .cpf("11111111111")
                .CEP("01310100")
                .phone("11987654321")
                .alreadyRecurrent(true)
                .inFirstLogin(false)
                .avatarUrl("https://i.pravatar.cc/150?img=1")
                .hasWhatsApp(true)
                .userType(UserType.ADMINISTRATOR)
                .build();

        userRepository.save(userMapper.toEntity(user1));

//        User user2 = User.builder()
//                .name("João")
//                .lastName("Silva")
//                .email("joao.silva@email.com")
//                .password(defaultPassword)
//                .address("Av. Paulista, 1000")
//                .cpf("22222222222")
//                .CEP("01310200")
//                .phone("11987654322")
//                .alreadyRecurrent(true)
//                .inFirstLogin(false)
//                .avatarUrl("https://i.pravatar.cc/150?img=12")
//                .hasWhatsApp(true)
//                .userType(UserType.CUSTOMER)
//                .build();
//
//        userRepository.save(UserJpaEntity.fromDomain(user2));
//
//        User user3 = User.builder()
//                .name("Maria")
//                .lastName("Santos")
//                .email("maria.santos@email.com")
//                .password(defaultPassword)
//                .address("Rua das Flores, 250")
//                .cpf("33333333333")
//                .CEP("01310300")
//                .phone("11987654323")
//                .alreadyRecurrent(true)
//                .inFirstLogin(false)
//                .avatarUrl("https://i.pravatar.cc/150?img=5")
//                .hasWhatsApp(true)
//                .userType(UserType.CUSTOMER)
//                .build();
//
//        userRepository.save(UserJpaEntity.fromDomain(user3));
//
//        User user4 = User.builder()
//                .name("Pedro")
//                .lastName("Almeida")
//                .email("pedro.almeida@email.com")
//                .password(defaultPassword)
//                .address("Av. Brasil, 500")
//                .cpf("44444444444")
//                .CEP("01310400")
//                .phone("11987654324")
//                .alreadyRecurrent(false)
//                .inFirstLogin(true)
//                .avatarUrl("https://i.pravatar.cc/150?img=8")
//                .hasWhatsApp(false)
//                .userType(UserType.CUSTOMER)
//                .build();
//
//        userRepository.save(UserJpaEntity.fromDomain(user4));

        User user5 = User.builder()
                .name("Lucas")
                .lastName("Furquim")
                .email("furquimmsw@gmail.com")
                .password(defaultPassword)
                .address("Rua dos Bobos, 0")
                .cpf("55555555555")
                .CEP("01310500")
                .phone("11987654325")
                .alreadyRecurrent(false)
                .inFirstLogin(true)
                .avatarUrl("https://i.pravatar.cc/150?img=9")
                .hasWhatsApp(true)
                .userType(UserType.CUSTOMER)
                .build();

        userRepository.save(userMapper.toEntity(user5));

        ComponentPart component1 = ComponentPart.builder()
                .name("Placa Principal")
                .description("Placa principal modelo X")
                .brand("Brand1")
                .price(150.00)
                .supplier("Supplier1")
                .category("Placas")
                .quantity(10L)
                .build();

        componentRepository.save(componentPartMapper.toEntity(component1));

        ComponentPart component2 = ComponentPart.builder()
                .name("Capacitor")
                .description("Capacitor 25uF")
                .brand("Brand2")
                .price(25.00)
                .supplier("Supplier2")
                .category("Capacitores")
                .quantity(50L)
                .build();

        componentRepository.save(componentPartMapper.toEntity(component2));

        ComponentPart component3 = ComponentPart.builder()
                .name("Motor")
                .description("Motor 1/2HP")
                .brand("Brand3")
                .price(200.00)
                .supplier("Supplier3")
                .category("Motores")
                .quantity(5L)
                .build();

        componentRepository.save(componentPartMapper.toEntity(component3));

        Announcement announcement1 = Announcement.builder()
                .title("Bem-vindo!")
                .content("Bem-vindo ao sistema UnderControl. Utilize suas credenciais para acessar.")
                .type(AnnouncementType.UPDATES)
                .publishedAt(LocalDateTime.now())
                .build();

        announcementRepository.save(announcementMapper.toEntity(announcement1));

        UserJpaEntity customer = userRepository.findUserByEmail("furquimmsw@gmail.com").orElse(null);

        ComponentPartJpaEntity compMotorEntity = ComponentPartJpaEntity.builder()
                .name("Motor")
                .description("Motor 1/2HP")
                .brand("Brand3")
                .price(200.00)
                .supplier("Supplier3")
                .category("Motores")
                .quantity(5L)
                .build();
        compMotorEntity = componentRepository.save(compMotorEntity);

        if (customer != null) {
            OrderItem orderItem = OrderItem.builder()
                    .imageUrl("https://i.pravatar.cc/150?img=10")
                    .observation("Troca de componente")
                    .volt("220V")
                    .series("ABC123")
                    .type("Eletrodomésticos")
                    .brand("Electrolux")
                    .model("DF80")
                    .laborValue(150.00)
                    .build();

            Demand demand = Demand.builder()
                    .quantity(2L)
                    .component(componentPartMapper.toDomain(compMotorEntity))
                    .build();

            Order order = Order.builder()
                    .user(userMapper.toDomain(customer))
                    .status(OrderStatus.PENDING)
                    .total(450.00)
                    .discount(0.0)
                    .fabricGuarantee(true)
                    .returnGuarantee(true)
                    .description("Reparo em placa principal")
                    .nf("NF-001")
                    .date(new Date())
                    .store("Loja Centro")
                    .build();

            order.getOrderItems().add(orderItem);
            order.getDemands().add(demand);

            orderRepository.save(orderMapper.toEntity(order));
        }

        log.info("Seed data initialized successfully!");
    }
}
