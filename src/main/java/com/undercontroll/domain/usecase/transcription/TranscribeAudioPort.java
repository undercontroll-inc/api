package com.undercontroll.domain.usecase.transcription;

import com.undercontroll.application.dto.transcription.TranscribeAudioRequest;
import com.undercontroll.application.dto.transcription.TranscribeAudioResponse;

public interface TranscribeAudioPort {

    TranscribeAudioResponse execute(TranscribeAudioRequest request);
}
