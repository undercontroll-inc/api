package com.undercontroll.application.dto.transcription;

import org.springframework.core.io.Resource;

public record TranscribeAudioRequest(
        Resource audio,
        String contentType,
        String filename,
        long size
) {
}
