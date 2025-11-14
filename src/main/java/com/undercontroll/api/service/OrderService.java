package com.undercontroll.api.service;

import com.undercontroll.api.dto.*;
import com.undercontroll.api.exception.*;
import com.undercontroll.api.model.*;
import com.undercontroll.api.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
        double partsTotal = 0.0;


        for (PartDto part : request.parts()) {
            Optional<ComponentPart> component = componentJpaRepository.findById(part.id());

            if (component.isEmpty()) {
                throw new ComponentNotFoundException("Could not found the component with id %s while creating a new order".formatted(part.id()));
            }

            if (component.get().getQuantity() < part.quantity()) {
                throw new InsuficientComponentException("Insufficient component quantity for this operation");
            }

            demandService.createDemand(
                    new CreateDemandRequest(
                            part.id(),
                            part.quantity()
                    )
            );

            // Atualiza a quantidade desse componente em estoque
            component.get().setQuantity(
                    component.get().getQuantity() - part.quantity()
            );

            componentJpaRepository.save(component.get());

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
        LocalDateTime receivedFormatted = LocalDateTime.parse(request.receivedAt());


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
                .total(total)
                .build();

        return repository.save(order);
    }

    public void updateOrder(UpdateOrderRequest request) {
        validateUpdateOrder(request);

        Optional<Order> orderFound = repository.findById(request.id());

        if(orderFound.isEmpty()) {
            throw new OrderNotFoundException("Could not found the order");
        }

        if(request.completedTime() != null) {
            orderFound.get().setCompletedTime(request.completedTime());
        }

        if(request.startedAt() != null) {
            orderFound.get().setStartedAt(request.startedAt());
        }

        repository.save(orderFound.get());
    }

    public List<OrderDto> getOrders() {
        return repository
                .findAll()
                .stream()
                .map(o -> new OrderDto(
                        o.getOrderItems()
                                .stream()
                                .map(orderItemService::mapToDto)
                                .toList(),
                        o.getCreatedAt(),
                        o.getStartedAt(),
                        o.getCompletedTime(),
                        o.getStatus()
                ))
                .toList();
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

        return new GetOrdersByUserIdResponse(
                orders.stream().map(o -> {
                    Double partsTotal = repository.calculatePartsTotalByUserId(userId);

                    List<OrderItemDto> items = new ArrayList<>();
                    Double totalLaborValue = 0.0;

                    for(OrderItem i : o.getOrderItems()) {
                        items.add(orderItemService.mapToDto(i));

                        totalLaborValue += i.getLaborValue();
                    }

                    // Valor total = total de todas as peças utilizadas + total de toda a mão de obra do pedido - o desconto
                    Double total = partsTotal + totalLaborValue - o.getDiscount();

                    return new OrderEnrichedDto(
                            o.getId(),
                            userId,
                            items,
                            partsTotal,
                            totalLaborValue,
                            o.getDiscount(),
                            total,
                            o.getCreatedAt().toString(),
                            o.getCompletedTime().toString(),
                            o.isReturnGuarantee(),
                            o.getDescription(),
                            null,
                            o.getStatus(),
                            o.getUpdatedAt().toString()
                    );
                }).toList()
        );
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

}
