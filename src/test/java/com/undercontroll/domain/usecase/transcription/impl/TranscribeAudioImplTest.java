package com.undercontroll.domain.usecase.transcription.impl;

import com.undercontroll.application.dto.transcription.TranscribeAudioResponse;
import com.undercontroll.domain.exception.InvalidTranscriptionException;
import com.undercontroll.domain.exception.TranscriptionUnavailableException;
import com.undercontroll.domain.gateway.AudioTranscriptionGateway;
import com.undercontroll.domain.usecase.transcription.TranscribeAudioPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.core.io.ByteArrayResource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TranscribeAudioImplTest {

    @Mock
    private ObjectProvider<AudioTranscriptionGateway> audioTranscriptionGateway;

    @Mock
    private AudioTranscriptionGateway gateway;

    @InjectMocks
    private TranscribeAudioImpl useCase;

    @Test
    @DisplayName("returns trimmed text from the gateway")
    void transcribes() {
        ByteArrayResource audio = audio("note.webm");
        when(audioTranscriptionGateway.getIfAvailable()).thenReturn(gateway);
        when(gateway.transcribe(eq(audio), eq("audio/webm"), eq("note.webm")))
                .thenReturn("  Geladeira não gela  ");

        TranscribeAudioResponse response = useCase.execute(new TranscribeAudioPort.Input(
                audio, "audio/webm", "note.webm", 12
        ));

        assertEquals("Geladeira não gela", response.text());
    }

    @Test
    @DisplayName("returns 503 when no transcription gateway is configured")
    void unavailableWithoutGateway() {
        when(audioTranscriptionGateway.getIfAvailable()).thenReturn(null);

        assertThrows(TranscriptionUnavailableException.class, () -> useCase.execute(
                new TranscribeAudioPort.Input(audio("note.webm"), "audio/webm", "note.webm", 12)
        ));
    }

    @Test
    @DisplayName("rejects an empty file")
    void rejectsEmptyFile() {
        assertThrows(InvalidTranscriptionException.class, () -> useCase.execute(
                new TranscribeAudioPort.Input(audio("note.webm"), "audio/webm", "note.webm", 0)
        ));
    }

    @Test
    @DisplayName("rejects an unsupported type")
    void rejectsUnsupportedType() {
        assertThrows(InvalidTranscriptionException.class, () -> useCase.execute(
                new TranscribeAudioPort.Input(audio("note.txt"), "text/plain", "note.txt", 12)
        ));
    }

    @Test
    @DisplayName("accepts octet-stream when the filename extension is audio")
    void acceptsOctetStreamWithAudioExtension() {
        ByteArrayResource audio = audio("note.m4a");
        when(audioTranscriptionGateway.getIfAvailable()).thenReturn(gateway);
        when(gateway.transcribe(any(), any(), any())).thenReturn("ok");

        TranscribeAudioResponse response = useCase.execute(new TranscribeAudioPort.Input(
                audio, "application/octet-stream", "note.m4a", 12
        ));

        assertEquals("ok", response.text());
    }

    @Test
    @DisplayName("wraps provider failures as unavailable")
    void wrapsProviderFailure() {
        when(audioTranscriptionGateway.getIfAvailable()).thenReturn(gateway);
        when(gateway.transcribe(any(), any(), any())).thenThrow(new IllegalStateException("timeout"));

        assertThrows(TranscriptionUnavailableException.class, () -> useCase.execute(
                new TranscribeAudioPort.Input(audio("note.webm"), "audio/webm", "note.webm", 12)
        ));
    }

    private static ByteArrayResource audio(String filename) {
        return new ByteArrayResource("audio-bytes".getBytes()) {
            @Override
            public String getFilename() {
                return filename;
            }
        };
    }
}
