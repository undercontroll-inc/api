package com.undercontroll.infrastructure.ai;

import com.undercontroll.domain.exception.TranscriptionUnavailableException;
import com.undercontroll.domain.gateway.AudioTranscriptionGateway;
import org.springframework.ai.audio.transcription.AudioTranscriptionPrompt;
import org.springframework.ai.audio.transcription.TranscriptionModel;
import org.springframework.ai.openai.OpenAiAudioTranscriptionOptions;
import org.springframework.core.io.Resource;
import org.springframework.util.StringUtils;

public class OpenAiAudioTranscriptionAdapter implements AudioTranscriptionGateway {

    private final TranscriptionModel transcriptionModel;

    public OpenAiAudioTranscriptionAdapter(TranscriptionModel transcriptionModel) {
        this.transcriptionModel = transcriptionModel;
    }

    @Override
    public String transcribe(Resource audio, String contentType, String filename) {
        try {
            var options = OpenAiAudioTranscriptionOptions.builder()
                    .language("pt")
                    .model("whisper-1")
                    .temperature(0f)
                    .build();
            String text = transcriptionModel.call(new AudioTranscriptionPrompt(audio, options))
                    .getResult()
                    .getOutput();
            if (!StringUtils.hasText(text)) {
                throw new TranscriptionUnavailableException();
            }
            return text.trim();
        } catch (TranscriptionUnavailableException ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw new TranscriptionUnavailableException(ex);
        }
    }
}
