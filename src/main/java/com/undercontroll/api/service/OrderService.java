package com.undercontroll.api.service;

import com.undercontroll.api.dto.*;
import com.undercontroll.api.exception.*;
import com.undercontroll.api.model.*;
import com.undercontroll.api.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderJpaRepository repository;

    // O ideal seria mover isso para uma service futuramente
    private final ComponentJpaRepository componentJpaRepository;

    private final OrderItemService orderItemService;
    private final DemandService demandService;
    private final UserService userService;

    public Order createOrder(
            CreateOrderRequest request
    ) {
        LocalDate completedFormatted = this.formatOrderDate(request.deadline());
        LocalDate receivedFormatted = this.formatOrderDate(request.receivedAt());

        double partsTotal = 0.0;
        List<PartDto> validatedParts = new ArrayList<>();

        // Validar as peças e calcular o total
        for (PartDto part : request.parts()) {
            Optional<ComponentPart> component = componentJpaRepository.findById(part.id());

            if (component.isEmpty()) {
                throw new ComponentNotFoundException("Could not found the component with id %s while creating a new order".formatted(part.id()));
            }

            if (component.get().getQuantity() < part.quantity()) {
                throw new InsuficientComponentException("Insufficient component quantity for this operation");
            }

            validatedParts.add(part);
            partsTotal += part.quantity() * component.get().getPrice();
        }

        List<OrderItem> orderItems = new ArrayList<>();
        Double laborTotal = 0.0;

        for (OrderItemCreateOrderRequest appliance : request.appliances()) {
            OrderItem orderItemCreated =
                    orderItemService.createOrderItem(
                            new CreateOrderItemRequest(
                                    appliance.brand(),
                                    appliance.model(),
                                    appliance.type(),
                                    "", // Futuramente viria a url da imagem uploadada,
                                    appliance.customerNote(),
                                    appliance.voltage(),
                                    appliance.serial(),
                                    appliance.laborValue()
                            )
                    );

            orderItems.add(orderItemCreated);
            laborTotal += orderItemCreated.getLaborValue();
        }

        User user = userService.getUserById(request.userId());
        Double total = partsTotal + laborTotal - request.discount();

        Order order = Order.builder()
                .orderItems(orderItems)
                .status(OrderStatus.PENDING)
                .user(user)
                .discount(request.discount())
                .date(null) // oque deveria ser posto aqui ?
                .store("Loja")
                .nf(request.nf())
                .fabricGuarantee(request.fabricGuarantee())
                .received_at(receivedFormatted)
                .description(request.serviceDescription())
                .returnGuarantee(request.returnGuarantee())
                .completedTime(completedFormatted)
                .total(total)
                .build();

        // Salvar o order primeiro para gerar o ID
        Order savedOrder = repository.save(order);

        // Agora criar as demands associadas ao order e atualizar o estoque
        for (PartDto part : validatedParts) {
            Optional<ComponentPart> component = componentJpaRepository.findById(part.id());

            if (component.isPresent()) {
                demandService.createDemand(
                        new CreateDemandRequest(
                                component.get(),
                                Long.valueOf(part.quantity()),
                                savedOrder
                        )
                );

                // Atualiza a quantidade desse componente em estoque
                component.get().setQuantity(
                        component.get().getQuantity() - part.quantity()
                );
                componentJpaRepository.save(component.get());
            }
        }

        return savedOrder;
    }

    // Ver isso aqui pois mudou muita coisa.
    public void updateOrder(UpdateOrderRequest request) {
        validateUpdateOrder(request);

        Optional<Order> orderFound = repository.findById(request.id());

        if(orderFound.isEmpty()) {
            throw new OrderNotFoundException("Could not found the order");
        }

        repository.save(orderFound.get());
    }

    public GetAllOrdersResponse getOrders() {

        // Caso receba o id de um usuário realiza o filtro
        List<Order> orders = repository.findAll();
        List<OrderEnrichedDto> data = new ArrayList<>();

        for (Order order : orders) {
            Double partsTotal = repository.calculatePartsTotalByOrderId(order.getId());

            List<OrderItemDto> items = new ArrayList<>();
            Double totalLaborValue = 0.0;

            for(OrderItem i : order.getOrderItems()) {
                items.add(orderItemService.mapToDto(i));

                totalLaborValue += i.getLaborValue();
            }

            // Valor total = total de todas as peças utilizadas + total de toda a mão de obra do pedido - o desconto
            Double total = partsTotal + totalLaborValue - order.getDiscount();
            List<ComponentDto> parts = getPartsByOrderId(order.getId());

            data.add(new OrderEnrichedDto(
                    order.getId(),
                    order.getUser().getId(),
                    items,
                    parts,
                    partsTotal,
                    totalLaborValue,
                    order.getDiscount(),
                    total,
                    order.getReceived_at() == null ? null :order.getReceived_at().toString(),
                    order.getCompletedTime() == null ? null :order.getCompletedTime().toString(),
                    order.getNf(),
                    order.isReturnGuarantee(),
                    order.getDescription(),
                    null,
                    order.getStatus(),
                    order.getUpdatedAt().toString()
            ));
        }

        return new GetAllOrdersResponse(
                data
        );
    }

    public void deleteOrder(Integer orderId) {
        validateDeleteOrder(orderId);

        Optional<Order> orderFound = repository.findById(orderId);

        if(orderFound.isEmpty()) {
            throw new OrderNotFoundException("Could not found the order");
        }

        repository.delete(orderFound.get());
    }

    public GetOrdersByUserIdResponse getOrdersByUserId(Integer userId) {

        // Caso receba o id de um usuário realiza o filtro
        List<Order> orders =
                userId == null
                        ? repository.findAll()
                        : repository.findByUser_id(userId);

        List<OrderEnrichedDto> data = new ArrayList<>();

        for (Order order : orders) {
            Double partsTotal = repository.calculatePartsTotalByOrderId(order.getId());

            List<OrderItemDto> items = new ArrayList<>();
            Double totalLaborValue = 0.0;

            for(OrderItem i : order.getOrderItems()) {
                items.add(orderItemService.mapToDto(i));

                totalLaborValue += i.getLaborValue();
            }

            // Valor total = total de todas as peças utilizadas + total de toda a mão de obra do pedido - o desconto
            Double total = partsTotal + totalLaborValue - order.getDiscount();

            List<ComponentDto> parts = getPartsByOrderId(order.getId());

            data.add(new OrderEnrichedDto(
                    order.getId(),
                    userId,
                    items,
                    parts,
                    partsTotal,
                    totalLaborValue,
                    order.getDiscount(),
                    total,
                    order.getReceived_at().toString(),
                    order.getCompletedTime().toString(),
                    order.getNf(),
                    order.isReturnGuarantee(),
                    order.getDescription(),
                    null,
                    order.getStatus(),
                    order.getUpdatedAt().toString()
            ));
        }

        return new GetOrdersByUserIdResponse(
                data
        );
    }

    public List<ComponentDto> getPartsByOrderId(Integer orderId) {
        List<Object[]> results = repository.findAllPartsByOrderIdNative(orderId);

        return results.stream()
                .map(row -> new ComponentDto(
                        (Integer) row[0],  // componentId
                        (String) row[1],   // name
                        (String) row[2],   // description
                        (String) row[3],   // brand
                        (Double) row[4],   // price
                        (Long) row[7],     // demandQuantity
                        (String) row[5],   // supplier
                        (String) row[6]   // category
                ))
                .toList();
    }

    private void validateUpdateOrder(UpdateOrderRequest request) {
        if(request.id() == null || request.id() <= 0){
            throw new InvalidUpdateOrderException("Order id cannot be null for the update");
        }
    }

    private void validateDeleteOrder(Integer orderId) {
        if(orderId == null || orderId <= 0){
            throw new InvalidDeleteOrderException("Order id cannot be null for the delete");
        }
    }

    private LocalDate formatOrderDate(String date) {
        try {
            if (date == null) return null;

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            return LocalDate.parse(date, formatter);

        } catch (Exception e) {
            throw new InvalidOrderDateException("The date format is invalid. Use dd/MM/yyyy");
        }
    }

}
