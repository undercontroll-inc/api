package com.undercontroll.infrastructure.service;

import java.util.Optional;

public interface StorageService {

    void putObject(String bucket, String key, byte[] data, Optional<String> contentType);

}
