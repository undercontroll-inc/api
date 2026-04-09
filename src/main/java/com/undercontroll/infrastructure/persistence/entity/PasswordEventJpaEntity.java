package com.undercontroll.infrastructure.persistence.entity;

import com.undercontroll.domain.enums.PasswordEventStatus;
import com.undercontroll.domain.enums.PasswordEventType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "password_event")
public class PasswordEventJpaEntity {

    @Id
    private UUID id;

    @Enumerated(EnumType.STRING)
    private PasswordEventType type;

    @Enumerated(EnumType.STRING)
    private PasswordEventStatus status;

    private String value;

    private String userPhone;

    private String userAgent;

    @CreationTimestamp
    private LocalDateTime createdAt;

}
