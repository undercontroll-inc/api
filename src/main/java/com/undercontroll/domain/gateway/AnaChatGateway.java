package com.undercontroll.domain.gateway;

public interface AnaChatGateway {

    String reply(String conversationId, String message, String shopBriefing);
}
