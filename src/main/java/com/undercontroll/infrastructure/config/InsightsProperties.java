package com.undercontroll.infrastructure.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "undercontroll.insights")
public class InsightsProperties {

    private String provider = "none";
    private String promptVersion = "4";
    private String cron = "0 0 4 1 * *";
    private String openaiModel = "gpt-4.1-mini";
    private String geminiModel = "gemini-2.5-flash";
    private int maxInsights = 7;
    private int repairCatalogDays = 90;

    public String resolvedProvider() {
        if (provider == null) {
            return "none";
        }
        return switch (provider.toLowerCase().trim()) {
            case "gemini", "google-genai", "google_genai" -> "gemini";
            case "openai" -> "openai";
            default -> "none";
        };
    }

    public String activeModel() {
        return "gemini".equals(resolvedProvider()) ? geminiModel : openaiModel;
    }
}
