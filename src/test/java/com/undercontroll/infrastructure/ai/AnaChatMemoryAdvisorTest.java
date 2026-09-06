package com.undercontroll.infrastructure.ai;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AnaChatMemoryAdvisorTest {

    @Mock
    private ChatMemory chatMemory;

    @Mock
    private CallAdvisorChain chain;

    @Mock
    private ChatClientResponse response;

    @Test
    @DisplayName("merges system messages into one, then history and the new user message")
    void ordersMessages() {
        when(chatMemory.get("7")).thenReturn(List.of(
                new UserMessage("antes"),
                new AssistantMessage("ok")
        ));
        ChatClientRequest request = new ChatClientRequest(
                new Prompt(List.of(
                        new SystemMessage("persona"),
                        new SystemMessage("briefing"),
                        new UserMessage("agora")
                )),
                Map.of(ChatMemory.CONVERSATION_ID, "7")
        );
        ArgumentCaptor<ChatClientRequest> forwarded = ArgumentCaptor.forClass(ChatClientRequest.class);
        when(chain.nextCall(forwarded.capture())).thenReturn(response);
        when(response.chatResponse()).thenReturn(null);

        AnaChatMemoryAdvisor advisor = new AnaChatMemoryAdvisor(chatMemory);
        assertSame(response, advisor.adviseCall(request, chain));

        List<Message> messages = forwarded.getValue().prompt().getInstructions();
        assertEquals(MessageType.SYSTEM, messages.get(0).getMessageType());
        assertEquals("persona\n\nbriefing", messages.get(0).getText());
        assertEquals("antes", messages.get(1).getText());
        assertEquals("ok", messages.get(2).getText());
        assertEquals("agora", messages.get(3).getText());
        verify(chatMemory).add(eq("7"), any(UserMessage.class));
    }
}
