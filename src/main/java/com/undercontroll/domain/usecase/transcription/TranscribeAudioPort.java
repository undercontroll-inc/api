package com.undercontroll.domain.usecase.transcription;

import com.undercontroll.application.dto.transcription.TranscribeAudioResponse;
import org.springframework.core.io.Resource;

public interface TranscribeAudioPort {

    record Input(Resource audio, String contentType, String filename, long size) {
    }

    TranscribeAudioResponse execute(Input input);
}
