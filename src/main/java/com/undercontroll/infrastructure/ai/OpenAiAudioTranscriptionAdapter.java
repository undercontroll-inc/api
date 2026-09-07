package com.undercontroll.infrastructure.ai;

import com.undercontroll.domain.exception.TranscriptionUnavailableException;
import com.undercontroll.domain.gateway.AudioTranscriptionGateway;
import com.undercontroll.infrastructure.logging.LogTiming;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.audio.transcription.AudioTranscriptionPrompt;
import org.springframework.ai.audio.transcription.TranscriptionModel;
import org.springframework.ai.openai.OpenAiAudioTranscriptionOptions;
import org.springframework.core.io.Resource;
import org.springframework.util.StringUtils;

@Slf4j
public class OpenAiAudioTranscriptionAdapter implements AudioTranscriptionGateway {

    static final String MODEL = "whisper-1";

    private final TranscriptionModel transcriptionModel;

    public OpenAiAudioTranscriptionAdapter(TranscriptionModel transcriptionModel) {
        this.transcriptionModel = transcriptionModel;
    }

    @Override
    public String transcribe(Resource audio, String contentType, String filename) {
        long started = System.nanoTime();
        long byteSize = contentLength(audio);
        log.info("OpenAI transcription started model={} mime={} byteSize={}", MODEL, contentType, byteSize);
        try {
            var options = OpenAiAudioTranscriptionOptions.builder()
                    .language("pt")
                    .model(MODEL)
                    .temperature(0f)
                    .build();
            String text = transcriptionModel.call(new AudioTranscriptionPrompt(audio, options))
                    .getResult()
                    .getOutput();
            if (!StringUtils.hasText(text)) {
                log.warn(
                        "OpenAI transcription returned empty text model={} durationMs={}",
                        MODEL,
                        LogTiming.millisSince(started)
                );
                throw new TranscriptionUnavailableException();
            }
            log.info(
                    "OpenAI transcription finished model={} chars={} durationMs={}",
                    MODEL,
                    text.trim().length(),
                    LogTiming.millisSince(started)
            );
            return text.trim();
        } catch (TranscriptionUnavailableException ex) {
            throw ex;
        } catch (RuntimeException ex) {
            log.warn(
                    "OpenAI transcription failed model={} durationMs={} cause={}",
                    MODEL,
                    LogTiming.millisSince(started),
                    ex.toString()
            );
            throw new TranscriptionUnavailableException(ex);
        }
    }

    private static long contentLength(Resource audio) {
        try {
            return audio.contentLength();
        } catch (Exception ex) {
            return -1L;
        }
    }
}
