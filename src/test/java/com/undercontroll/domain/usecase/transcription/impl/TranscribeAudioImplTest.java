package com.undercontroll.domain.usecase.transcription.impl;

import com.undercontroll.application.dto.transcription.TranscribeAudioRequest;
import com.undercontroll.application.dto.transcription.TranscribeAudioResponse;
import com.undercontroll.application.mapper.TranscriptionDtoMapper;
import com.undercontroll.domain.exception.InvalidTranscriptionException;
import com.undercontroll.domain.exception.TranscriptionUnavailableException;
import com.undercontroll.domain.gateway.AudioTranscriptionGateway;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.core.io.ByteArrayResource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TranscribeAudioImplTest {

    @Mock
    private ObjectProvider<AudioTranscriptionGateway> audioTranscriptionGateway;

    @Mock
    private AudioTranscriptionGateway gateway;

    @Spy
    private TranscriptionDtoMapper transcriptionDtoMapper = new TranscriptionDtoMapper();

    @InjectMocks
    private TranscribeAudioImpl useCase;

    @Test
    @DisplayName("returns trimmed text from the gateway")
    void transcribes() {
        ByteArrayResource audio = audio("note.webm");
        when(audioTranscriptionGateway.getIfAvailable()).thenReturn(gateway);
        when(gateway.transcribe(audio, "audio/webm", "note.webm"))
                .thenReturn("  Geladeira não gela  ");

        TranscribeAudioResponse response = useCase.execute(new TranscribeAudioRequest(
                audio, "audio/webm", "note.webm", 12
        ));

        assertEquals("Geladeira não gela", response.text());
    }

    @Test
    @DisplayName("returns 503 when no transcription gateway is configured")
    void unavailableWithoutGateway() {
        when(audioTranscriptionGateway.getIfAvailable()).thenReturn(null);
        TranscribeAudioRequest request = new TranscribeAudioRequest(
                audio("note.webm"), "audio/webm", "note.webm", 12
        );

        assertThrows(TranscriptionUnavailableException.class, () -> useCase.execute(request));
    }

    @Test
    @DisplayName("rejects an empty file")
    void rejectsEmptyFile() {
        TranscribeAudioRequest request = new TranscribeAudioRequest(
                audio("note.webm"), "audio/webm", "note.webm", 0
        );

        assertThrows(InvalidTranscriptionException.class, () -> useCase.execute(request));
    }

    @Test
    @DisplayName("rejects an unsupported type")
    void rejectsUnsupportedType() {
        TranscribeAudioRequest request = new TranscribeAudioRequest(
                audio("note.txt"), "text/plain", "note.txt", 12
        );

        assertThrows(InvalidTranscriptionException.class, () -> useCase.execute(request));
    }

    @Test
    @DisplayName("accepts octet-stream when the filename extension is audio")
    void acceptsOctetStreamWithAudioExtension() {
        ByteArrayResource audio = audio("note.m4a");
        when(audioTranscriptionGateway.getIfAvailable()).thenReturn(gateway);
        when(gateway.transcribe(any(), any(), any())).thenReturn("ok");

        TranscribeAudioResponse response = useCase.execute(new TranscribeAudioRequest(
                audio, "application/octet-stream", "note.m4a", 12
        ));

        assertEquals("ok", response.text());
    }

    @Test
    @DisplayName("wraps provider failures as unavailable")
    void wrapsProviderFailure() {
        when(audioTranscriptionGateway.getIfAvailable()).thenReturn(gateway);
        when(gateway.transcribe(any(), any(), any())).thenThrow(new IllegalStateException("timeout"));
        TranscribeAudioRequest request = new TranscribeAudioRequest(
                audio("note.webm"), "audio/webm", "note.webm", 12
        );

        assertThrows(TranscriptionUnavailableException.class, () -> useCase.execute(request));
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
