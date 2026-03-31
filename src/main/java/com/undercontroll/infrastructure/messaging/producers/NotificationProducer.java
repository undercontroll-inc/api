package com.undercontroll.infrastructure.messaging.producers;

import com.undercontroll.application.port.NotificationPort;
import com.undercontroll.domain.events.AnnouncementCreatedEvent;
import com.undercontroll.domain.events.UserCreatedEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class NotificationProducer implements NotificationPort {

    private final RabbitTemplate rabbitTemplate;

    @Value("${spring.rabbitmq.ex.notification}")
    private String notificationExchange;

    @Value("${spring.rabbitmq.routing-key.announcement}")
    private String announcementRoutingKey;

    @Value("${spring.rabbitmq.routing-key.user-created}")
    private String userCreatedRoutingKey;


    @Async
    @Override
    public void handleAnnouncementCreated(AnnouncementCreatedEvent event) {
        var data = Map.of(
                "id", event.announcement().getId(),
                "title", event.announcement().getTitle(),
                "content", event.announcement().getContent(),
                "type", event.announcement().getType().name(),
                "publishedAt", event.announcement().getPublishedAt().toString(),
                "token", event.token() != null ? event.token() : ""
        );
        
        var payload = Map.of(
                "service", "main-service",
                "type", "ANNOUNCEMENT_CREATED",
                "data", data,
                "timestamp", java.time.LocalDateTime.now().toString()
        );
        rabbitTemplate.convertAndSend(notificationExchange, announcementRoutingKey, payload);
    }

    @Async
    @Override
    public void handleUserCreated(UserCreatedEvent event) {
        LocalDateTime createdAt = event.createdAt() != null ? event.createdAt() : LocalDateTime.now();

        var data = Map.of(
                "name", event.name() != null ? event.name() : "",
                "email", event.email() != null ? event.email() : "",
                "createdAt", createdAt.toString()
        );

        var payload = Map.of(
                "service", "main-service",
                "type", "USER_CREATED",
                "data", data,
                "timestamp", LocalDateTime.now().toString()
        );

        rabbitTemplate.convertAndSend(notificationExchange, userCreatedRoutingKey, payload);
    }
}
