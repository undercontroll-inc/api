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
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ShopBriefingAdvisorTest {

    @Mock
    private CallAdvisorChain chain;

    @Mock
    private ChatClientResponse response;

    @Test
    @DisplayName("appends the shop briefing as a system message")
    void appendsBriefing() {
        ChatClientRequest request = new ChatClientRequest(
                new Prompt(new UserMessage("Quais consertos estão abertos?")),
                Map.of(ShopBriefingAdvisor.PARAM, "pedido 12, cliente Maria")
        );
        ArgumentCaptor<ChatClientRequest> forwarded = ArgumentCaptor.forClass(ChatClientRequest.class);
        when(chain.nextCall(forwarded.capture())).thenReturn(response);

        ShopBriefingAdvisor advisor = new ShopBriefingAdvisor();
        assertSame(response, advisor.adviseCall(request, chain));

        Message last = forwarded.getValue().prompt().getInstructions().getLast();
        assertInstanceOf(SystemMessage.class, last);
        assertTrue(last.getText().contains("Maria"));
        assertEquals("Quais consertos estão abertos?", forwarded.getValue().prompt().getUserMessage().getText());
    }

    @Test
    @DisplayName("passes through when there is no briefing")
    void skipsBlank() {
        ChatClientRequest request = new ChatClientRequest(
                new Prompt(new UserMessage("Oi")),
                Map.of()
        );
        when(chain.nextCall(request)).thenReturn(response);

        assertSame(response, new ShopBriefingAdvisor().adviseCall(request, chain));
        verify(chain).nextCall(request);
    }
}
