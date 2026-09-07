package com.undercontroll.domain.usecase.transcription.impl;

import com.undercontroll.application.dto.transcription.TranscribeAudioResponse;
import com.undercontroll.domain.exception.InvalidTranscriptionException;
import com.undercontroll.domain.exception.TranscriptionUnavailableException;
import com.undercontroll.domain.gateway.AudioTranscriptionGateway;
import com.undercontroll.domain.usecase.transcription.TranscribeAudioPort;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Locale;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class TranscribeAudioImpl implements TranscribeAudioPort {

    static final long MAX_BYTES = 8L * 1024 * 1024;

    private static final Set<String> ALLOWED_TYPES = Set.of(
            "audio/webm",
            "audio/mpeg",
            "audio/mp3",
            "audio/mp4",
            "audio/m4a",
            "audio/x-m4a",
            "audio/wav",
            "audio/x-wav",
            "audio/wave",
            "audio/ogg"
    );

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
            "webm", "mp3", "mpeg", "mp4", "m4a", "wav", "ogg"
    );

    private final ObjectProvider<AudioTranscriptionGateway> audioTranscriptionGateway;

    @Override
    public TranscribeAudioResponse execute(Input input) {
        validate(input);
        AudioTranscriptionGateway gateway = audioTranscriptionGateway.getIfAvailable();
        if (gateway == null) {
            throw new TranscriptionUnavailableException();
        }
        try {
            String text = gateway.transcribe(input.audio(), input.contentType(), input.filename());
            if (!StringUtils.hasText(text)) {
                throw new TranscriptionUnavailableException();
            }
            return new TranscribeAudioResponse(text.trim());
        } catch (TranscriptionUnavailableException | InvalidTranscriptionException ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw new TranscriptionUnavailableException(ex);
        }
    }

    private static void validate(Input input) {
        Resource audio = input == null ? null : input.audio();
        if (audio == null) {
            throw new InvalidTranscriptionException("Audio file is required");
        }
        if (input.size() <= 0) {
            throw new InvalidTranscriptionException("Audio file is empty");
        }
        if (input.size() > MAX_BYTES) {
            throw new InvalidTranscriptionException("Audio file exceeds 8MB");
        }
        if (!allowed(input.contentType(), input.filename())) {
            throw new InvalidTranscriptionException("Unsupported audio type");
        }
    }

    static boolean allowed(String contentType, String filename) {
        String type = mediaType(contentType);
        if (type != null && ALLOWED_TYPES.contains(type)) {
            return true;
        }
        String extension = extension(filename);
        return extension != null && ALLOWED_EXTENSIONS.contains(extension);
    }

    private static String mediaType(String contentType) {
        if (!StringUtils.hasText(contentType)) {
            return null;
        }
        String type = contentType.split(";", 2)[0].trim().toLowerCase(Locale.ROOT);
        return type.isEmpty() || "application/octet-stream".equals(type) ? null : type;
    }

    private static String extension(String filename) {
        if (!StringUtils.hasText(filename)) {
            return null;
        }
        int dot = filename.lastIndexOf('.');
        if (dot < 0 || dot == filename.length() - 1) {
            return null;
        }
        return filename.substring(dot + 1).toLowerCase(Locale.ROOT);
    }
}
