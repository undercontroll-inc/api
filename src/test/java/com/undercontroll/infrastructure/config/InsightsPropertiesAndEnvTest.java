package com.undercontroll.infrastructure.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.mock.env.MockEnvironment;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InsightsPropertiesAndEnvTest {

    @Test
    @DisplayName("normalizes provider aliases and picks the active model")
    void providerAliases() {
        InsightsProperties properties = new InsightsProperties();
        properties.setProvider("gemini");
        properties.setGeminiModel("gemini-3.6-flash");
        properties.setOpenaiModel("gpt-4.1-mini");
        assertEquals("gemini", properties.resolvedProvider());
        assertEquals("gemini-3.6-flash", properties.activeModel());

        properties.setProvider("openai");
        assertEquals("openai", properties.resolvedProvider());
        assertEquals("gpt-4.1-mini", properties.activeModel());

        properties.setProvider("none");
        assertEquals("none", properties.resolvedProvider());
        properties.setProvider(null);
        assertEquals("none", properties.resolvedProvider());
        InsightsProperties defaults = new InsightsProperties();
        assertEquals(7, defaults.getMaxInsights());
        assertEquals("5", defaults.getPromptVersion());
    }

    @Test
    @DisplayName("maps INSIGHTS_PROVIDER to spring.ai.model.chat")
    void environmentPostProcessor() {
        InsightsAiEnvironmentPostProcessor processor = new InsightsAiEnvironmentPostProcessor();
        MockEnvironment env = new MockEnvironment();
        env.setProperty("undercontroll.insights.provider", "gemini");
        processor.postProcessEnvironment(env, new SpringApplication());
        assertEquals("google-genai", env.getProperty("spring.ai.model.chat"));
        assertEquals("none", env.getProperty("spring.ai.model.audio.transcription"));
        assertUnusedOpenAiModelsAreOff(env);

        MockEnvironment openai = new MockEnvironment();
        openai.setProperty("INSIGHTS_PROVIDER", "openai");
        processor.postProcessEnvironment(openai, new SpringApplication());
        assertEquals("openai", openai.getProperty("spring.ai.model.chat"));
        assertEquals("openai", openai.getProperty("spring.ai.model.audio.transcription"));
        assertUnusedOpenAiModelsAreOff(openai);

        MockEnvironment envOverridesYamlDefault = new MockEnvironment();
        envOverridesYamlDefault.setProperty("undercontroll.insights.provider", "none");
        envOverridesYamlDefault.setProperty("INSIGHTS_PROVIDER", "gemini");
        processor.postProcessEnvironment(envOverridesYamlDefault, new SpringApplication());
        assertEquals("google-genai", envOverridesYamlDefault.getProperty("spring.ai.model.chat"));
    }

    private static void assertUnusedOpenAiModelsAreOff(MockEnvironment env) {
        assertEquals("none", env.getProperty("spring.ai.model.audio.speech"));
        assertEquals("none", env.getProperty("spring.ai.model.embedding"));
        assertEquals("none", env.getProperty("spring.ai.model.embedding.text"));
        assertEquals("none", env.getProperty("spring.ai.model.image"));
        assertEquals("none", env.getProperty("spring.ai.model.moderation"));
    }
}
