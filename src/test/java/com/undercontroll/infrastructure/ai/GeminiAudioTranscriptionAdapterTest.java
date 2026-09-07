package com.undercontroll.infrastructure.ai;

import com.google.genai.Models;
import com.google.genai.types.Content;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.GenerateContentResponse;
import com.undercontroll.domain.exception.TranscriptionUnavailableException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.util.MimeType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GeminiAudioTranscriptionAdapterTest {

    @Mock
    private Models models;

    @Mock
    private GenerateContentResponse response;

    @Test
    @DisplayName("returns Gemini text")
    void transcribes() {
        when(models.generateContent(eq("gemini-3.5-flash-lite"), any(Content.class), any(GenerateContentConfig.class)))
                .thenReturn(response);
        when(response.text()).thenReturn("  Compressor ruindo ");

        String text = new GeminiAudioTranscriptionAdapter(models, "gemini-3.5-flash-lite")
                .transcribe(new ByteArrayResource("bytes".getBytes()), "audio/webm", "note.webm");

        assertEquals("Compressor ruindo", text);
    }

    @Test
    @DisplayName("treats blank Gemini output as unavailable")
    void blankOutput() {
        when(models.generateContent(eq("gemini-3.5-flash-lite"), any(Content.class), any(GenerateContentConfig.class)))
                .thenReturn(response);
        when(response.text()).thenReturn(" ");
        GeminiAudioTranscriptionAdapter adapter = new GeminiAudioTranscriptionAdapter(models, "gemini-3.5-flash-lite");
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
