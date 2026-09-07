package com.undercontroll.infrastructure.ai;

import com.google.genai.Client;
import com.undercontroll.domain.gateway.AudioTranscriptionGateway;
import org.springframework.ai.audio.transcription.TranscriptionModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

@AutoConfiguration(afterName = {
        "org.springframework.ai.model.openai.autoconfigure.OpenAiAudioTranscriptionAutoConfiguration",
        "org.springframework.ai.model.google.genai.autoconfigure.chat.GoogleGenAiChatAutoConfiguration",
        "org.springframework.ai.model.openai.autoconfigure.OpenAiChatAutoConfiguration"
})
public class AudioTranscriptionConfig {

    @Bean
    @ConditionalOnBean(TranscriptionModel.class)
    AudioTranscriptionGateway openAiAudioTranscriptionGateway(TranscriptionModel transcriptionModel) {
        return new OpenAiAudioTranscriptionAdapter(transcriptionModel);
    }

    @Bean
    @ConditionalOnBean(Client.class)
    @ConditionalOnMissingBean(AudioTranscriptionGateway.class)
    AudioTranscriptionGateway geminiAudioTranscriptionGateway(
            Client googleGenAiClient,
            @Value("${undercontroll.transcription.gemini-model:gemini-3.5-flash-lite}") String geminiModel
    ) {
        return new GeminiAudioTranscriptionAdapter(googleGenAiClient, geminiModel);
    }
}
