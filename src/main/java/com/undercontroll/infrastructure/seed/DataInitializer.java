package com.undercontroll.infrastructure.seed;

import com.undercontroll.domain.gateway.AnnouncementGateway;
import com.undercontroll.domain.gateway.ComponentGateway;
import com.undercontroll.domain.gateway.OrderGateway;
import com.undercontroll.domain.gateway.UserGateway;
import com.undercontroll.domain.model.User;
import com.undercontroll.domain.model.ComponentPart;
import com.undercontroll.domain.model.Announcement;
import com.undercontroll.domain.model.Order;
import com.undercontroll.domain.model.OrderItem;
import com.undercontroll.domain.model.Demand;
import com.undercontroll.domain.enums.AnnouncementType;
import com.undercontroll.domain.enums.UserType;
import com.undercontroll.domain.enums.OrderStatus;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer {

    private final UserGateway userGateway;
    private final ComponentGateway componentGateway;
    private final AnnouncementGateway announcementGateway;
    private final OrderGateway orderGateway;

    private final PasswordEncoder encoder;

    @PostConstruct
    public void init() {
        log.info("Initializing seed data...");

        if (!userGateway.findAll().isEmpty()) {
            log.info("Database already seeded, skipping...");
            return;
        }

        String defaultPassword = encoder.encode("123");

        // ── Users ─────────────────────────────────────────────────────────────

        User admin = User.builder()
                .name("Admin").lastName("Sistema")
                .email("admin@undercontroll.com").password(defaultPassword)
                .address("Rua Principal, 100").cpf("11111111111").CEP("01310100")
                .phone("11987654321").alreadyRecurrent(true).inFirstLogin(false)
                .avatarUrl("https://i.pravatar.cc/150?img=1")
                .hasWhatsApp(true).userType(UserType.ADMINISTRATOR)
                .build();
        userGateway.save(admin);

        User lucas = User.builder()
                .name("Lucas").lastName("Furquim")
                .email("furquimmsw@gmail.com").password(defaultPassword)
                .address("Rua dos Bobos, 0").cpf("55555555555").CEP("01310500")
                .phone("11987654325").alreadyRecurrent(false).inFirstLogin(true)
                .avatarUrl("https://i.pravatar.cc/150?img=9")
                .hasWhatsApp(true).userType(UserType.CUSTOMER)
                .build();
        lucas = userGateway.save(lucas);

        User joao = User.builder()
                .name("João").lastName("Silva")
                .email("joao.silva@email.com").password(defaultPassword)
                .address("Av. Paulista, 1000").cpf("22222222222").CEP("01310200")
                .phone("11987654322").alreadyRecurrent(true).inFirstLogin(false)
                .avatarUrl("https://i.pravatar.cc/150?img=12")
                .hasWhatsApp(true).userType(UserType.CUSTOMER)
                .build();
        joao = userGateway.save(joao);

        User maria = User.builder()
                .name("Maria").lastName("Santos")
                .email("maria.santos@email.com").password(defaultPassword)
                .address("Rua das Flores, 250").cpf("33333333333").CEP("01310300")
                .phone("11987654323").alreadyRecurrent(true).inFirstLogin(false)
                .avatarUrl("https://i.pravatar.cc/150?img=5")
                .hasWhatsApp(true).userType(UserType.CUSTOMER)
                .build();
        maria = userGateway.save(maria);

        User pedro = User.builder()
                .name("Pedro").lastName("Almeida")
                .email("pedro.almeida@email.com").password(defaultPassword)
                .address("Av. Brasil, 500").cpf("44444444444").CEP("01310400")
                .phone("11987654324").alreadyRecurrent(false).inFirstLogin(false)
                .avatarUrl("https://i.pravatar.cc/150?img=8")
                .hasWhatsApp(false).userType(UserType.CUSTOMER)
                .build();
        pedro = userGateway.save(pedro);

        User ana = User.builder()
                .name("Ana").lastName("Costa")
                .email("ana.costa@email.com").password(defaultPassword)
                .address("Rua das Acácias, 77").cpf("66666666666").CEP("01310600")
                .phone("11987654326").alreadyRecurrent(true).inFirstLogin(false)
                .avatarUrl("https://i.pravatar.cc/150?img=47")
                .hasWhatsApp(true).userType(UserType.CUSTOMER)
                .build();
        ana = userGateway.save(ana);

        // ── Components ────────────────────────────────────────────────────────

        ComponentPart placaPrincipal = componentGateway.save(ComponentPart.builder()
                .name("Placa Principal").description("Placa principal modelo X")
                .brand("Electrolux").price(150.00).supplier("TechParts").category("Placas").quantity(10L)
                .build());

        ComponentPart capacitor = componentGateway.save(ComponentPart.builder()
                .name("Capacitor 25uF").description("Capacitor eletrolítico 25uF 400V")
                .brand("Nichicon").price(18.00).supplier("ElecSupply").category("Capacitores").quantity(80L)
                .build());

        ComponentPart motor = componentGateway.save(ComponentPart.builder()
                .name("Motor 1/2HP").description("Motor elétrico universal 1/2HP")
                .brand("WEG").price(220.00).supplier("MotorBras").category("Motores").quantity(8L)
                .build());

        ComponentPart resistencia = componentGateway.save(ComponentPart.builder()
                .name("Resistência").description("Resistência de chuveiro/aquecedor 220V")
                .brand("Thermex").price(45.00).supplier("ThermoSupply").category("Resistências").quantity(30L)
                .build());

        ComponentPart compressor = componentGateway.save(ComponentPart.builder()
                .name("Compressor").description("Compressor para geladeira 1/5HP")
                .brand("Embraco").price(380.00).supplier("FrigoParts").category("Compressores").quantity(5L)
                .build());

        ComponentPart placaControle = componentGateway.save(ComponentPart.builder()
                .name("Placa de Controle").description("Placa de controle eletrônico universal")
                .brand("Samsung").price(210.00).supplier("TechParts").category("Placas").quantity(12L)
                .build());

        ComponentPart sensor = componentGateway.save(ComponentPart.builder()
                .name("Sensor de Temperatura").description("Sensor NTC para geladeiras e freezers")
                .brand("Bosch").price(35.00).supplier("SensorBras").category("Sensores").quantity(40L)
                .build());

        // ── Announcement ─────────────────────────────────────────────────────

        announcementGateway.save(Announcement.builder()
                .title("Bem-vindo!")
                .content("Bem-vindo ao sistema UnderControl. Utilize suas credenciais para acessar.")
                .type(AnnouncementType.UPDATES)
                .publishedAt(LocalDateTime.now())
                .build());

        // ── Orders ────────────────────────────────────────────────────────────
        // Spread across different periods so all dashboard filters return data.
        // received_at drives period filters; completedTime enables repair time calc.

        LocalDate today = LocalDate.now();

        // Last 7 days ─────────────────────────────────────────────────────────

        saveOrder(joao, OrderStatus.COMPLETED,
                today.minusDays(2), today.minusDays(1), 520.00,
                "Geladeira", "Electrolux", "DF80", "220V",
                List.of(entry(compressor, 1L), entry(sensor, 1L)));

        saveOrder(maria, OrderStatus.DELIVERED,
                today.minusDays(4), today.minusDays(2), 280.00,
                "Máquina de Lavar", "Brastemp", "BWB11", "220V",
                List.of(entry(motor, 1L), entry(capacitor, 2L)));

        saveOrder(pedro, OrderStatus.PENDING,
                today.minusDays(3), null, 180.00,
                "Microondas", "LG", "MS3052R", "110V",
                List.of(entry(placaControle, 1L)));

        saveOrder(lucas, OrderStatus.IN_ANALYSIS,
                today.minusDays(1), null, 95.00,
                "Fogão", "Consul", "CF4GB", "220V",
                List.of(entry(resistencia, 2L)));

        // Last 30 days ────────────────────────────────────────────────────────

        saveOrder(ana, OrderStatus.DELIVERED,
                today.minusDays(10), today.minusDays(7), 760.00,
                "Ar-condicionado", "Samsung", "AR12", "220V",
                List.of(entry(placaControle, 1L), entry(capacitor, 3L)));

        saveOrder(joao, OrderStatus.COMPLETED,
                today.minusDays(12), today.minusDays(9), 340.00,
                "Geladeira", "Brastemp", "BRM44", "220V",
                List.of(entry(sensor, 2L), entry(capacitor, 1L)));

        saveOrder(maria, OrderStatus.COMPLETED,
                today.minusDays(15), today.minusDays(13), 440.00,
                "Máquina de Lavar", "LG", "WT7WE", "220V",
                List.of(entry(motor, 1L), entry(placaPrincipal, 1L)));

        saveOrder(pedro, OrderStatus.DELIVERED,
                today.minusDays(18), today.minusDays(15), 620.00,
                "TV", "Samsung", "UN50TU8", "110V",
                List.of(entry(placaControle, 1L), entry(placaPrincipal, 1L)));

        saveOrder(lucas, OrderStatus.PENDING,
                today.minusDays(20), null, 130.00,
                "Microondas", "Electrolux", "MEB41", "110V",
                List.of(entry(resistencia, 1L)));

        saveOrder(ana, OrderStatus.IN_ANALYSIS,
                today.minusDays(22), null, 210.00,
                "Fogão", "Brastemp", "BF4GB", "220V",
                List.of(entry(resistencia, 2L), entry(sensor, 1L)));

        // Last 90 days ────────────────────────────────────────────────────────

        saveOrder(joao, OrderStatus.DELIVERED,
                today.minusDays(35), today.minusDays(30), 890.00,
                "Ar-condicionado", "LG", "TS-Q122", "220V",
                List.of(entry(compressor, 1L), entry(placaControle, 1L)));

        saveOrder(maria, OrderStatus.COMPLETED,
                today.minusDays(40), today.minusDays(36), 310.00,
                "Geladeira", "Consul", "CRM44", "220V",
                List.of(entry(sensor, 1L), entry(capacitor, 2L)));

        saveOrder(pedro, OrderStatus.DELIVERED,
                today.minusDays(50), today.minusDays(44), 680.00,
                "Máquina de Lavar", "Electrolux", "LTC15", "220V",
                List.of(entry(motor, 1L), entry(resistencia, 1L)));

        saveOrder(ana, OrderStatus.COMPLETED,
                today.minusDays(55), today.minusDays(51), 420.00,
                "TV", "LG", "50UP7750", "110V",
                List.of(entry(placaPrincipal, 1L), entry(capacitor, 4L)));

        saveOrder(lucas, OrderStatus.DELIVERED,
                today.minusDays(60), today.minusDays(54), 540.00,
                "Geladeira", "Samsung", "RT50K", "220V",
                List.of(entry(compressor, 1L), entry(sensor, 2L)));

        saveOrder(joao, OrderStatus.COMPLETED,
                today.minusDays(70), today.minusDays(65), 255.00,
                "Microondas", "Brastemp", "BMG45", "110V",
                List.of(entry(placaControle, 1L)));

        // Older (this year / previous year) ───────────────────────────────────

        saveOrder(maria, OrderStatus.DELIVERED,
                today.minusDays(120), today.minusDays(115), 730.00,
                "Ar-condicionado", "Electrolux", "QI18F", "220V",
                List.of(entry(compressor, 1L), entry(capacitor, 2L)));

        saveOrder(pedro, OrderStatus.COMPLETED,
                today.minusDays(150), today.minusDays(144), 390.00,
                "Máquina de Lavar", "Consul", "CWL16", "220V",
                List.of(entry(motor, 1L), entry(sensor, 1L)));

        saveOrder(ana, OrderStatus.DELIVERED,
                today.minusDays(200), today.minusDays(193), 870.00,
                "Geladeira", "LG", "GN-B502", "220V",
                List.of(entry(compressor, 1L), entry(placaPrincipal, 1L), entry(sensor, 1L)));

        saveOrder(lucas, OrderStatus.COMPLETED,
                today.minusDays(250), today.minusDays(244), 460.00,
                "TV", "Electrolux", "TVM43", "110V",
                List.of(entry(placaControle, 1L), entry(capacitor, 3L)));

        log.info("Seed data initialized successfully!");
    }

    private record DemandEntry(ComponentPart component, Long quantity) {}

    private DemandEntry entry(ComponentPart component, Long quantity) {
        return new DemandEntry(component, quantity);
    }

    private void saveOrder(User customer, OrderStatus status,
                           LocalDate receivedAt, LocalDate completedTime,
                           Double total, String itemType, String itemBrand,
                           String itemModel, String volt,
                           List<DemandEntry> demands) {
        Order order = Order.builder()
                .user(customer)
                .status(status)
                .total(total)
                .discount(0.0)
                .fabricGuarantee(true)
                .returnGuarantee(true)
                .description("Reparo em " + itemType)
                .nf("NF-" + (int)(Math.random() * 9000 + 1000))
                .date(new Date())
                .store("Loja Centro")
                .received_at(receivedAt)
                .completedTime(completedTime)
                .build();

        OrderItem item = OrderItem.builder()
                .imageUrl("https://i.pravatar.cc/150?img=" + (int)(Math.random() * 70 + 1))
                .observation("Reparo solicitado pelo cliente")
                .volt(volt)
                .series("SN" + (int)(Math.random() * 90000 + 10000))
                .type(itemType)
                .brand(itemBrand)
                .model(itemModel)
                .laborValue(total * 0.3)
                .build();

        order.addOrderItem(item);

        for (DemandEntry d : demands) {
            order.addDemand(Demand.builder()
                    .quantity(d.quantity())
                    .component(d.component())
                    .build());
        }

        orderGateway.save(order);
    }
}
