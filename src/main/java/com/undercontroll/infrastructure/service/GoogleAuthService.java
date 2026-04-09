package com.undercontroll.infrastructure.service;

public interface GoogleAuthService {
    boolean verify(String idToken, String expectedEmail);
}
