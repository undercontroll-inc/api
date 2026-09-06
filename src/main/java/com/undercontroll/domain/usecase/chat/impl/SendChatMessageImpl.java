package com.undercontroll.domain.usecase.chat.impl;

import com.undercontroll.application.dto.chat.SendChatMessageRequest;
import com.undercontroll.application.dto.chat.SendChatMessageResponse;
import com.undercontroll.domain.exception.AnaUnavailableException;
import com.undercontroll.domain.gateway.AnaChatGateway;
import com.undercontroll.domain.gateway.CurrentUserIdPort;
import com.undercontroll.domain.gateway.OrderGateway;
import com.undercontroll.domain.model.Order;
import com.undercontroll.domain.model.chat.ShopSuggestionComposer;
import com.undercontroll.domain.usecase.chat.SendChatMessagePort;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SendChatMessageImpl implements SendChatMessagePort {

    private final ObjectProvider<AnaChatGateway> anaChatGateway;
    private final CurrentUserIdPort currentUserIdPort;
    private final ShopSnapshotLoader shopSnapshotLoader;
    private final OrderGateway orderGateway;

    @Override
    public SendChatMessageResponse execute(SendChatMessageRequest request) {
        AnaChatGateway llm = anaChatGateway.getIfAvailable();
        if (llm == null) {
            throw new AnaUnavailableException();
        }
        Integer userId = currentUserIdPort.require();
        String briefing = ShopSuggestionComposer.chatBriefing(shopSnapshotLoader.load());
        List<Order> cited = ShopSuggestionComposer.mentionedOrderIds(request.content()).stream()
                .map(orderGateway::findDetailById)
                .flatMap(Optional::stream)
                .toList();
        briefing = ShopSuggestionComposer.appendOrderDetails(briefing, cited);
        String content = llm.reply(String.valueOf(userId), request.content(), briefing);
        return new SendChatMessageResponse(content);
    }
}
