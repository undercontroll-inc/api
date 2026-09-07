package com.undercontroll.infrastructure.ai;

import com.undercontroll.domain.exception.AnaUnavailableException;
import com.undercontroll.domain.gateway.AnaChatGateway;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;

import java.util.List;

public class SpringAiAnaChatAdapter implements AnaChatGateway {

    private final ChatClient anaChatClient;
    private final ChatMemory anaChatMemory;
    private final AnaWebSearchTool anaWebSearchTool;
    private final AnaShopTools anaShopTools;

    public SpringAiAnaChatAdapter(
            ChatClient anaChatClient,
            ChatMemory anaChatMemory,
            AnaWebSearchTool anaWebSearchTool,
            AnaShopTools anaShopTools
    ) {
        this.anaChatClient = anaChatClient;
        this.anaChatMemory = anaChatMemory;
        this.anaWebSearchTool = anaWebSearchTool;
        this.anaShopTools = anaShopTools;
    }

    @Override
    public String reply(String conversationId, String message, String shopBriefing) {
        try {
            List<String> history = recentConversationTexts(conversationId);
            var spec = anaChatClient.prompt()
                    .advisors(advisor -> advisor
                            .param(ChatMemory.CONVERSATION_ID, conversationId)
                            .param(ShopBriefingAdvisor.PARAM, shopBriefing == null ? "" : shopBriefing))
                    .user(message);
            if (AnaWebSearchNeed.matchesConversation(message, history)) {
                spec = spec.tools(anaShopTools, anaWebSearchTool);
            } else {
                spec = spec.tools(anaShopTools);
            }
            String content = spec.call().content();
            if (content == null || content.isBlank()) {
                throw new AnaUnavailableException();
            }
            return content;
        } catch (AnaUnavailableException ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw new AnaUnavailableException(ex);
        }
    }

    @Override
    public List<String> recentConversationTexts(String conversationId) {
        if (conversationId == null || conversationId.isBlank() || anaChatMemory == null) {
            return List.of();
        }
        try {
            return anaChatMemory.get(conversationId).stream()
                    .map(Message::getText)
                    .filter(text -> text != null && !text.isBlank())
                    .toList();
        } catch (RuntimeException ex) {
            return List.of();
        }
    }
}
