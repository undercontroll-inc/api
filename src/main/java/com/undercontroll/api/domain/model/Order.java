package com.undercontroll.api.domain.model;

import java.time.LocalDateTime;
import java.util.List;

public class Order {

    private Integer id;
    private List<OrderItem> orderItems;
    private LocalDateTime startedAt;
    private LocalDateTime completedTime;


}
