package com.undercontroll.infrastructure.ai;

import com.undercontroll.domain.gateway.AnnouncementGateway;
import com.undercontroll.domain.gateway.ComponentGateway;
import com.undercontroll.domain.gateway.OrderGateway;
import com.undercontroll.domain.model.Announcement;
import com.undercontroll.domain.model.ComponentPart;
import com.undercontroll.domain.model.Demand;
import com.undercontroll.domain.model.Order;
import com.undercontroll.domain.model.OrderItem;
import com.undercontroll.domain.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class AnaShopTools {

    public static final int LIMIT = 15;

    private final OrderGateway orderGateway;
    private final ComponentGateway componentGateway;
    private final AnnouncementGateway announcementGateway;

    @Tool(description = "Detalhe de um conserto/pedido pelo id, com itens e peças pedidas.")
    public OrderDetail getOrder(Integer orderId) {
        try {
            return orderGateway.findDetailById(orderId)
                    .map(AnaShopTools::toOrderDetail)
                    .orElse(null);
        } catch (RuntimeException ex) {
            return null;
        }
    }

    @Tool(description = "Busca peças do estoque por nome ou categoria. Se ambos vazios, lista as de menor estoque.")
    public List<PartLine> searchComponents(String name, String category) {
        List<ComponentPart> parts;
        if (name != null && !name.isBlank()) {
            parts = componentGateway.searchByName(name, LIMIT);
        } else if (category != null && !category.isBlank()) {
            parts = componentGateway.searchByCategory(category, LIMIT);
        } else {
            parts = componentGateway.findLowestStock(LIMIT);
        }
        return parts.stream().limit(LIMIT).map(AnaShopTools::toPartLine).toList();
    }

    @Tool(description = "Último aviso do aplicativo.")
    public AnnouncementLine getLastAnnouncement() {
        return announcementGateway.findLastAnnouncement()
                .map(AnaShopTools::toAnnouncementLine)
                .orElse(null);
    }

    private static OrderLine toOrderLine(Order order) {
        User user = order.getUser();
        String customer = null;
        if (user != null) {
            String joined = String.join(" ",
                    Objects.toString(user.getName(), ""),
                    Objects.toString(user.getLastName(), "")).trim();
            if (!joined.isBlank()) {
                customer = joined;
            }
        }
        int items = order.getOrderItems() == null ? 0 : order.getOrderItems().size();
        return new OrderLine(
                order.getId(),
                order.getStatus() == null ? null : order.getStatus().name(),
                customer,
                order.getDescription(),
                items
        );
    }

    private static OrderDetail toOrderDetail(Order order) {
        OrderLine header = toOrderLine(order);
        List<ItemLine> items = order.getOrderItems() == null
                ? List.of()
                : order.getOrderItems().stream().limit(LIMIT).map(AnaShopTools::toItemLine).toList();
        List<DemandLine> demands = order.getDemands() == null
                ? List.of()
                : order.getDemands().stream().limit(LIMIT).map(AnaShopTools::toDemandLine).toList();
        return new OrderDetail(header, items, demands, order.getTotal(), order.getStore());
    }

    private static ItemLine toItemLine(OrderItem item) {
        return new ItemLine(
                item.getId(),
                item.getBrand(),
                item.getModel(),
                item.getType(),
                item.getObservation(),
                item.getLaborValue()
        );
    }

    private static PartLine toPartLine(ComponentPart part) {
        return new PartLine(
                part.getId(),
                part.getName(),
                part.getBrand(),
                part.getCategory(),
                part.getQuantity(),
                part.getPrice(),
                part.getSupplier()
        );
    }

    private static DemandLine toDemandLine(Demand demand) {
        ComponentPart part = demand.getComponent();
        Integer orderId = demand.getOrder() == null ? null : demand.getOrder().getId();
        return new DemandLine(
                demand.getId(),
                orderId,
                part == null ? null : part.getId(),
                part == null ? null : part.getName(),
                demand.getQuantity()
        );
    }

    private static AnnouncementLine toAnnouncementLine(Announcement announcement) {
        return new AnnouncementLine(
                announcement.getId(),
                announcement.getTitle(),
                announcement.getContent(),
                announcement.getType() == null ? null : announcement.getType().name()
        );
    }

    public record OrderLine(Integer id, String status, String customer, String description, int itemCount) {
    }

    public record OrderDetail(OrderLine order, List<ItemLine> items, List<DemandLine> demands, Double total, String store) {
    }

    public record ItemLine(Integer id, String brand, String model, String type, String observation, Double laborValue) {
    }

    public record PartLine(
            Integer id,
            String name,
            String brand,
            String category,
            Long quantity,
            Double price,
            String supplier
    ) {
    }

    public record DemandLine(Integer id, Integer orderId, Integer componentId, String componentName, Long quantity) {
    }

    public record AnnouncementLine(Integer id, String title, String content, String type) {
    }
}
