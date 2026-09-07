package com.undercontroll.domain.gateway;

import org.springframework.core.io.Resource;

public interface AudioTranscriptionGateway {

    String transcribe(Resource audio, String contentType, String filename);
}
