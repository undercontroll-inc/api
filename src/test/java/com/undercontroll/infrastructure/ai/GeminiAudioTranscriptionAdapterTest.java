package com.undercontroll.infrastructure.ai;

import com.undercontroll.domain.exception.TranscriptionUnavailableException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.util.MimeType;

import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GeminiAudioTranscriptionAdapterTest {

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ChatClient chatClient;

    @Test
    @DisplayName("returns Gemini text")
    @SuppressWarnings("unchecked")
    void transcribes() {
        when(chatClient.prompt().user(any(Consumer.class)).call().content())
                .thenReturn("  Compressor ruindo ");

        String text = new GeminiAudioTranscriptionAdapter(chatClient)
                .transcribe(new ByteArrayResource("bytes".getBytes()), "audio/webm", "note.webm");

        assertEquals("Compressor ruindo", text);
    }

    @Test
    @DisplayName("treats blank Gemini output as unavailable")
    @SuppressWarnings("unchecked")
    void blankOutput() {
        when(chatClient.prompt().user(any(Consumer.class)).call().content()).thenReturn(" ");
        GeminiAudioTranscriptionAdapter adapter = new GeminiAudioTranscriptionAdapter(chatClient);
        ByteArrayResource audio = new ByteArrayResource("bytes".getBytes());

        assertThrows(TranscriptionUnavailableException.class, () ->
                adapter.transcribe(audio, "audio/webm", "note.webm"));
    }

    @Test
    @DisplayName("infers mime type from the filename when the content type is missing")
    void infersMimeFromFilename() {
        assertEquals(MimeType.valueOf("audio/mpeg"), GeminiAudioTranscriptionAdapter.mimeType(null, "note.mp3"));
        assertEquals(MimeType.valueOf("audio/mp4"), GeminiAudioTranscriptionAdapter.mimeType("application/octet-stream", "clip.m4a"));
        assertEquals(MimeType.valueOf("audio/webm"), GeminiAudioTranscriptionAdapter.mimeType(null, "note.webm"));
    }
}
