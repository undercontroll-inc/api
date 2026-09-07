package com.undercontroll.application.controller.impl;

import com.undercontroll.application.controller.TranscriptionApi;
import com.undercontroll.application.dto.transcription.TranscribeAudioResponse;
import com.undercontroll.domain.exception.InvalidTranscriptionException;
import com.undercontroll.domain.usecase.transcription.TranscribeAudioPort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class TranscriptionController implements TranscriptionApi {

    private final TranscribeAudioPort transcribeAudioPort;

    @Override
    public ResponseEntity<TranscribeAudioResponse> transcribe(MultipartFile audio) {
        if (audio == null) {
            throw new InvalidTranscriptionException("Audio file is required");
        }
        return ResponseEntity.ok(transcribeAudioPort.execute(new TranscribeAudioPort.Input(
                audio.getResource(),
                audio.getContentType(),
                audio.getOriginalFilename(),
                audio.getSize()
        )));
    }
}
