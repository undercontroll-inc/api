package com.undercontroll.infrastructure.ai;

import com.undercontroll.domain.gateway.AudioTranscriptionGateway;
import org.springframework.ai.audio.transcription.TranscriptionModel;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;

import java.nio.charset.StandardCharsets;

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

    @Bean(name = "transcriptionChatClient")
    @ConditionalOnBean(ChatModel.class)
    @ConditionalOnProperty(name = "spring.ai.model.chat", havingValue = "google-genai")
    ChatClient transcriptionChatClient(
            ChatModel chatModel,
            @Value("classpath:prompts/transcription-system-prompt.txt") Resource systemPrompt,
            @Value("${spring.ai.model.chat:none}") String modelName,
            @Value("${spring.ai.google.genai.chat.options.model:gemini-2.5-flash}") String geminiModel
    ) {
        return ChatClient.builder(chatModel)
                .defaultSystem(systemPrompt, StandardCharsets.UTF_8)
                .defaultOptions(AiChatOptions.transcription(modelName, geminiModel))
                .build();
    }

    @Bean
    @ConditionalOnBean(name = "transcriptionChatClient")
    @ConditionalOnMissingBean(AudioTranscriptionGateway.class)
    AudioTranscriptionGateway geminiAudioTranscriptionGateway(
            @Qualifier("transcriptionChatClient") ChatClient transcriptionChatClient
    ) {
        return new GeminiAudioTranscriptionAdapter(transcriptionChatClient);
    }
}
