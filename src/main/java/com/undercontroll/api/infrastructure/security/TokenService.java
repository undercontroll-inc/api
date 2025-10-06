package com.undercontroll.api.infrastructure.security;

public interface TokenService {

    String generateToken(String username);

}
