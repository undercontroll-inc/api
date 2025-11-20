package com.undercontroll.api.service;

public interface MetricsService {

    void incrementLoginFailed();

    void incrementLoginSuccess();

    void incrementGoogleLoginFailed();

    void incrementGoogleLoginSuccess();

    void incrementAccountCreated();

    void incrementAccountCreationFailed();

    void incrementOrderCreated();

    void incrementOrderCompleted();

    void incrementOrderCancelled();

    void incrementOrderUpdateFailed();

    void recordOrderProcessingTime(long startTimeMillis);

    void incrementInsufficientStock(String componentName);

    void incrementComponentCreated();

    void incrementComponentUpdated();

    void incrementStockDecreased();

    void incrementDemandCreated();

    void incrementDemandRemoved();

    void incrementEmailSent();

    void incrementEmailFailed();

    void incrementAnnouncementCreated();

    void incrementAnnouncementEmailsSent(int count);
}

