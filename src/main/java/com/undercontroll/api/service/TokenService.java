package com.undercontroll.api.service;

public interface TokenService {

    String generateToken(String username);

    String extractUsername(String token);
}
