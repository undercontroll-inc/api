package com.undercontroll.infrastructure.ai;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.UserMessage;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SpringAiAnaChatAdapterTest {

    @Mock
    private ChatClient chatClient;

    @Mock
    private ChatMemory chatMemory;

    @Mock
    private AnaWebSearchTool webSearchTool;

    @Mock
    private AnaShopTools shopTools;

    @Test
    @DisplayName("reads conversation texts so follow-ups can reuse cited orders")
    void recentConversationTexts() {
        when(chatMemory.get("7")).thenReturn(List.of(
                new UserMessage("Me fala do pedido 12"),
                new AssistantMessage("O liquidificador da Maria ainda não foi olhado.")
        ));
        SpringAiAnaChatAdapter adapter = new SpringAiAnaChatAdapter(
                chatClient, chatMemory, webSearchTool, shopTools);

        assertEquals(
                List.of("Me fala do pedido 12", "O liquidificador da Maria ainda não foi olhado."),
                adapter.recentConversationTexts("7"));
        assertEquals(List.of(), adapter.recentConversationTexts(" "));
    }
}
