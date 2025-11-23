package com.undercontroll.api.service;

import com.undercontroll.api.model.enums.UserType;

public interface TokenService {

    String generateToken(String username, UserType userType);

    String extractUsername(String token);
}
