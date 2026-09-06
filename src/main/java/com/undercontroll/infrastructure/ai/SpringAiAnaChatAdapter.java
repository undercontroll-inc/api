package com.undercontroll.infrastructure.ai;

import com.undercontroll.domain.exception.AnaUnavailableException;
import com.undercontroll.domain.gateway.AnaChatGateway;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;

public class SpringAiAnaChatAdapter implements AnaChatGateway {

    private final ChatClient anaChatClient;
    private final AnaWebSearchTool anaWebSearchTool;
    private final AnaShopTools anaShopTools;

    public SpringAiAnaChatAdapter(
            ChatClient anaChatClient,
            AnaWebSearchTool anaWebSearchTool,
            AnaShopTools anaShopTools
    ) {
        this.anaChatClient = anaChatClient;
        this.anaWebSearchTool = anaWebSearchTool;
        this.anaShopTools = anaShopTools;
    }

    @Override
    public String reply(String conversationId, String message, String shopBriefing) {
        try {
            var spec = anaChatClient.prompt()
                    .advisors(advisor -> advisor
                            .param(ChatMemory.CONVERSATION_ID, conversationId)
                            .param(ShopBriefingAdvisor.PARAM, shopBriefing == null ? "" : shopBriefing))
                    .user(message);
            if (AnaShopToolNeed.matches(message)) {
                spec = spec.tools(anaShopTools);
            }
            if (AnaWebSearchNeed.matches(message)) {
                spec = spec.tools(anaWebSearchTool);
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
}
