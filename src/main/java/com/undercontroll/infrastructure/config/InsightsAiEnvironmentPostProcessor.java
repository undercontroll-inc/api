package com.undercontroll.infrastructure.config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.config.ConfigDataEnvironmentPostProcessor;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class InsightsAiEnvironmentPostProcessor implements EnvironmentPostProcessor, Ordered {

    static final String PROPERTY_SOURCE_NAME = "insightsAiChatModel";

    private static final Log log = LogFactory.getLog(InsightsAiEnvironmentPostProcessor.class);

    @Override
    public int getOrder() {
        return ConfigDataEnvironmentPostProcessor.ORDER + 10;
    }

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        String provider = firstNonBlank(
                environment.getProperty("INSIGHTS_PROVIDER"),
                environment.getProperty("undercontroll.insights.provider")
        );
        if (!StringUtils.hasText(provider)) {
            provider = "none";
        }
        String chatModel = switch (provider.toLowerCase(Locale.ROOT).trim()) {
            case "openai" -> "openai";
            case "gemini", "google-genai", "google_genai" -> "google-genai";
            default -> "none";
        };
        Map<String, Object> map = new HashMap<>();
        map.put("spring.ai.model.chat", chatModel);
        disableUnusedModels(map);
        environment.getPropertySources().addFirst(new MapPropertySource(PROPERTY_SOURCE_NAME, map));
        log.info("Insights provider='" + provider + "' -> spring.ai.model.chat=" + chatModel);
    }

    private static String firstNonBlank(String... values) {
        for (String value : values) {
            if (StringUtils.hasText(value) && !"none".equalsIgnoreCase(value.trim())) {
                return value;
            }
        }
        return null;
    }

    private static void disableUnusedModels(Map<String, Object> map) {
        map.put("spring.ai.model.embedding", "none");
        map.put("spring.ai.model.embedding.text", "none");
        map.put("spring.ai.model.embedding.multimodal", "none");
        map.put("spring.ai.model.image", "none");
        map.put("spring.ai.model.moderation", "none");
        map.put("spring.ai.model.audio.speech", "none");
        map.put("spring.ai.model.audio.transcription", "none");
    }
}
