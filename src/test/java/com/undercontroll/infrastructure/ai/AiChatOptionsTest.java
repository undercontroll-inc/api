package com.undercontroll.infrastructure.ai;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.google.genai.GoogleGenAiChatOptions;
import org.springframework.ai.google.genai.common.GoogleGenAiThinkingLevel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;

class AiChatOptionsTest {

    @Test
    @DisplayName("Ana uses low thinking on Gemini 3")
    void anaUsesLowThinkingOnGemini3() {
        GoogleGenAiChatOptions ana = assertInstanceOf(
                GoogleGenAiChatOptions.class,
                AiChatOptions.ana("google-genai", "gemini-3.6-flash")
        );
        assertEquals(GoogleGenAiThinkingLevel.LOW, ana.getThinkingLevel());
        assertNull(ana.getThinkingBudget());
    }

    @Test
    @DisplayName("Ana disables thinking budget on Gemini 2.5")
    void anaDisablesThinkingOnGemini25() {
        GoogleGenAiChatOptions ana = assertInstanceOf(
                GoogleGenAiChatOptions.class,
                AiChatOptions.ana("google-genai", "gemini-2.5-flash")
        );
        assertEquals(0, ana.getThinkingBudget());
        assertNull(ana.getThinkingLevel());
    }

    @Test
    @DisplayName("insights does not set a thinking level")
    void insightsLeavesThinkingUnset() {
        ChatOptions options = AiChatOptions.insights("google-genai", "gemini-2.5-flash");
        GoogleGenAiChatOptions gemini = assertInstanceOf(GoogleGenAiChatOptions.class, options);
        assertNull(gemini.getThinkingLevel());
        assertNull(gemini.getThinkingBudget());
    }
}
