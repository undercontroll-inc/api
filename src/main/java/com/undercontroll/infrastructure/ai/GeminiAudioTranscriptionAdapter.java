package com.undercontroll.infrastructure.ai;

import com.undercontroll.domain.exception.TranscriptionUnavailableException;
import com.undercontroll.domain.gateway.AudioTranscriptionGateway;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.content.Media;
import org.springframework.core.io.Resource;
import org.springframework.util.MimeType;
import org.springframework.util.StringUtils;

import java.util.Locale;

public class GeminiAudioTranscriptionAdapter implements AudioTranscriptionGateway {

    static final String USER_PROMPT =
            "Transcreva fielmente o áudio em português do Brasil. Responda só com o texto falado, sem aspas nem comentários.";

    private final ChatClient transcriptionChatClient;

    public GeminiAudioTranscriptionAdapter(ChatClient transcriptionChatClient) {
        this.transcriptionChatClient = transcriptionChatClient;
    }

    @Override
    public String transcribe(Resource audio, String contentType, String filename) {
        try {
            MimeType mimeType = mimeType(contentType, filename);
            String content = transcriptionChatClient.prompt()
                    .user(user -> user.text(USER_PROMPT).media(new Media(mimeType, audio)))
                    .call()
                    .content();
            if (!StringUtils.hasText(content)) {
                throw new TranscriptionUnavailableException();
            }
            return content.trim();
        } catch (TranscriptionUnavailableException ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw new TranscriptionUnavailableException(ex);
        }
    }

    static MimeType mimeType(String contentType, String filename) {
        String type = mediaType(contentType);
        if (type != null) {
            return MimeType.valueOf(type);
        }
        String name = filename == null ? "" : filename.toLowerCase(Locale.ROOT);
        if (name.endsWith(".mp3") || name.endsWith(".mpeg")) {
            return MimeType.valueOf("audio/mpeg");
        }
        if (name.endsWith(".mp4") || name.endsWith(".m4a")) {
            return MimeType.valueOf("audio/mp4");
        }
        if (name.endsWith(".wav")) {
            return MimeType.valueOf("audio/wav");
        }
        if (name.endsWith(".ogg")) {
            return MimeType.valueOf("audio/ogg");
        }
        return MimeType.valueOf("audio/webm");
    }

    private static String mediaType(String contentType) {
        if (!StringUtils.hasText(contentType)) {
            return null;
        }
        String type = contentType.split(";", 2)[0].trim().toLowerCase(Locale.ROOT);
        if (type.isEmpty() || "application/octet-stream".equals(type)) {
            return null;
        }
        return type;
    }
}
