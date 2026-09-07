package com.undercontroll.domain.gateway;

import java.util.List;

public interface AnaChatGateway {

    String reply(String conversationId, String message, String shopBriefing);

    default List<String> recentConversationTexts(String conversationId) {
        return List.of();
    }
}
