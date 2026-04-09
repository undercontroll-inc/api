package com.undercontroll.domain.usecase.auth;

public interface RefreshTokenPort {

    record Input(String refreshToken) {}

    record Output(String accessToken, String refreshToken) {}

    Output execute(Input input);
}

