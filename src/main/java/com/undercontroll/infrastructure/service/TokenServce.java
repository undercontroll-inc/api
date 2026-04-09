package com.undercontroll.infrastructure.service;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.undercontroll.domain.enums.UserType;

public interface TokenServce {

    String generateToken(String username, UserType userType);
    DecodedJWT validateToken(String token);
    String extractUsername(String token);
}
