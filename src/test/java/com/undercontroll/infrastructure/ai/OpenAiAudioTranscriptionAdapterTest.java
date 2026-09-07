package com.undercontroll.infrastructure.ai;

import com.undercontroll.domain.exception.TranscriptionUnavailableException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.audio.transcription.AudioTranscription;
import org.springframework.ai.audio.transcription.AudioTranscriptionPrompt;
import org.springframework.ai.audio.transcription.AudioTranscriptionResponse;
import org.springframework.ai.audio.transcription.TranscriptionModel;
import org.springframework.core.io.ByteArrayResource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OpenAiAudioTranscriptionAdapterTest {

    @Mock
    private TranscriptionModel transcriptionModel;

    @Test
    @DisplayName("returns Whisper text")
    void transcribes() {
        ByteArrayResource audio = new ByteArrayResource("bytes".getBytes());
        when(transcriptionModel.call(any(AudioTranscriptionPrompt.class)))
                .thenReturn(new AudioTranscriptionResponse(new AudioTranscription("  Não gela ")));

        String text = new OpenAiAudioTranscriptionAdapter(transcriptionModel)
                .transcribe(audio, "audio/webm", "note.webm");

        assertEquals("Não gela", text);
    }

    @Test
    @DisplayName("treats blank Whisper output as unavailable")
    void blankOutput() {
        when(transcriptionModel.call(any(AudioTranscriptionPrompt.class)))
                .thenReturn(new AudioTranscriptionResponse(new AudioTranscription(" ")));

        assertThrows(TranscriptionUnavailableException.class, () ->
                new OpenAiAudioTranscriptionAdapter(transcriptionModel)
                        .transcribe(new ByteArrayResource("bytes".getBytes()), "audio/webm", "note.webm"));
    }

    @Test
    @DisplayName("wraps provider errors")
    void wrapsErrors() {
        when(transcriptionModel.call(any(AudioTranscriptionPrompt.class)))
                .thenThrow(new IllegalStateException("whisper down"));

        assertThrows(TranscriptionUnavailableException.class, () ->
                new OpenAiAudioTranscriptionAdapter(transcriptionModel)
                        .transcribe(new ByteArrayResource("bytes".getBytes()), "audio/webm", "note.webm"));
    }
}
