package com.undercontroll.infrastructure.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "undercontroll.insights")
public class InsightsProperties {

    private static final String PROVIDER_NONE = "none";
    private static final String PROVIDER_GEMINI = "gemini";
    private static final String PROVIDER_OPENAI = "openai";

    private String provider = PROVIDER_NONE;
    private String promptVersion = "4";
    private String cron = "0 0 4 1 * *";
    private String openaiModel = "gpt-4.1-mini";
    private String geminiModel = "gemini-2.5-flash";
    private int maxInsights = 7;
    private int repairCatalogDays = 90;

    public String resolvedProvider() {
        if (provider == null) {
            return PROVIDER_NONE;
        }
        return switch (provider.toLowerCase().trim()) {
            case PROVIDER_GEMINI, "google-genai", "google_genai" -> PROVIDER_GEMINI;
            case PROVIDER_OPENAI -> PROVIDER_OPENAI;
            default -> PROVIDER_NONE;
        };
    }

    public String activeModel() {
        return PROVIDER_GEMINI.equals(resolvedProvider()) ? geminiModel : openaiModel;
    }
}
