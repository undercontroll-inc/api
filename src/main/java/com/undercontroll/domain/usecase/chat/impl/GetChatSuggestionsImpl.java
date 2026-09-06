package com.undercontroll.domain.usecase.chat.impl;

import com.undercontroll.application.dto.chat.ChatSuggestionsResponse;
import com.undercontroll.domain.gateway.AnaSuggestionStore;
import com.undercontroll.domain.gateway.CurrentUserIdPort;
import com.undercontroll.domain.model.chat.ShopSuggestionComposer;
import com.undercontroll.domain.usecase.chat.GetChatSuggestionsPort;
import com.undercontroll.infrastructure.config.AnaProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetChatSuggestionsImpl implements GetChatSuggestionsPort {

    private final AnaSuggestionStore anaSuggestionStore;
    private final ShopSnapshotLoader shopSnapshotLoader;
    private final AnaProperties anaProperties;
    private final CurrentUserIdPort currentUserIdPort;

    @Override
    public ChatSuggestionsResponse execute(boolean refresh) {
        Integer userId = currentUserIdPort.require();
        var cached = anaSuggestionStore.findByUserId(userId);
        if (!refresh && cached.isPresent()) {
            return new ChatSuggestionsResponse(cached.get());
        }
        int count = Math.max(1, anaProperties.getSuggestionCount());
        List<String> suggestions = ShopSuggestionComposer.groundedQuestions(shopSnapshotLoader.load(), count);
        anaSuggestionStore.save(userId, suggestions);
        return new ChatSuggestionsResponse(suggestions);
    }
}
