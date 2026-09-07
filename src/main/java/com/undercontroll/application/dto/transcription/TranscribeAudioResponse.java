package com.undercontroll.application.dto.transcription;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Transcribed speech")
public record TranscribeAudioResponse(
        @Schema(description = "Plain text of the voice note", example = "Geladeira não gela desde ontem")
        String text
) {
}
