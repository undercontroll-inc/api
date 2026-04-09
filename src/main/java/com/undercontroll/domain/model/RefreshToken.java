package com.undercontroll.domain.model;

import lombok.*;

import java.time.Instant;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RefreshToken {

    private Long id;

    private String token;

    private Integer userId;

    private String userEmail;

    private String userRole;

    private Instant expiresAt;

    private boolean revoked;
}
