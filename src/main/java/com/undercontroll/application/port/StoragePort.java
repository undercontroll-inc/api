package com.undercontroll.application.port;

import java.util.Optional;

public interface StoragePort {

    void putObject(String bucket, String key, byte[] data, Optional<String> contentType);

}
