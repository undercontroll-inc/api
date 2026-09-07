package com.undercontroll.infrastructure.ai;

import com.undercontroll.domain.exception.AnaUnavailableException;
import com.undercontroll.domain.gateway.AnaChatGateway;
import com.undercontroll.infrastructure.logging.LogTiming;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;

import java.util.List;

@Slf4j
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
        long started = System.nanoTime();
        boolean webSearch = false;
        try {
            List<String> history = recentConversationTexts(conversationId);
            webSearch = AnaWebSearchNeed.matchesConversation(message, history);
            String tools = webSearch ? "shop+web" : "shop";
            log.info("Ana chat started conversationId={} tools={}", conversationId, tools);
            var spec = anaChatClient.prompt()
                    .advisors(advisor -> advisor
                            .param(ChatMemory.CONVERSATION_ID, conversationId)
                            .param(ShopBriefingAdvisor.PARAM, shopBriefing == null ? "" : shopBriefing))
                    .user(message);
            if (webSearch) {
                spec = spec.tools(anaShopTools, anaWebSearchTool);
            } else {
                spec = spec.tools(anaShopTools);
            }
            String content = spec.call().content();
            if (content == null || content.isBlank()) {
                throw new AnaUnavailableException();
            }
            log.info(
                    "Ana chat finished conversationId={} tools={} durationMs={}",
                    conversationId,
                    tools,
                    LogTiming.millisSince(started)
            );
            return content;
        } catch (AnaUnavailableException ex) {
            log.warn(
                    "Ana chat unavailable conversationId={} durationMs={}",
                    conversationId,
                    LogTiming.millisSince(started)
            );
            throw ex;
        } catch (RuntimeException ex) {
            log.warn(
                    "Ana chat failed conversationId={} durationMs={} cause={}",
                    conversationId,
                    LogTiming.millisSince(started),
                    ex.toString()
            );
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
            log.warn("Ana chat memory read failed conversationId={} cause={}", conversationId, ex.toString());
            return List.of();
        }
    }
}
