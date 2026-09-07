package com.undercontroll.application.mapper;

import com.undercontroll.application.dto.transcription.TranscribeAudioRequest;
import com.undercontroll.application.dto.transcription.TranscribeAudioResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class TranscriptionDtoMapperTest {

    private final TranscriptionDtoMapper mapper = new TranscriptionDtoMapper();

    @Test
    @DisplayName("maps a multipart file to a transcription request")
    void toRequest() {
        MockMultipartFile audio = new MockMultipartFile(
                "audio", "note.webm", "audio/webm", "voice".getBytes()
        );

        TranscribeAudioRequest request = mapper.toRequest(audio);

        assertNotNull(request.audio());
        assertEquals("audio/webm", request.contentType());
        assertEquals("note.webm", request.filename());
        assertEquals(5, request.size());
    }

    @Test
    @DisplayName("maps null audio to a null request")
    void toRequestNull() {
        assertNull(mapper.toRequest(null));
    }

    @Test
    @DisplayName("maps transcribed text to a response")
    void toResponse() {
        TranscribeAudioResponse response = mapper.toResponse("Geladeira não gela");

        assertEquals("Geladeira não gela", response.text());
    }
}
