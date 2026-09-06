package com.undercontroll.domain.usecase.chat;

import com.undercontroll.application.dto.chat.SendChatMessageRequest;
import com.undercontroll.application.dto.chat.SendChatMessageResponse;

public interface SendChatMessagePort {
    SendChatMessageResponse execute(SendChatMessageRequest request);
}
