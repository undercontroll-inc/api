package com.undercontroll.application.mapper;

import com.undercontroll.application.dto.transcription.TranscribeAudioRequest;
import com.undercontroll.application.dto.transcription.TranscribeAudioResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class TranscriptionDtoMapper {

    public TranscribeAudioRequest toRequest(MultipartFile audio) {
        if (audio == null) {
            return null;
        }
        return new TranscribeAudioRequest(
                audio.getResource(),
                audio.getContentType(),
                audio.getOriginalFilename(),
                audio.getSize()
        );
    }

    public TranscribeAudioResponse toResponse(String text) {
        return new TranscribeAudioResponse(text);
    }
}
