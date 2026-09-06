package com.undercontroll.domain.usecase.chat;

import com.undercontroll.application.dto.chat.ChatSuggestionsResponse;

public interface GetChatSuggestionsPort {
    ChatSuggestionsResponse execute(boolean refresh);
}
