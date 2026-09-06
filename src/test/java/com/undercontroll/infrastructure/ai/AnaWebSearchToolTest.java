package com.undercontroll.infrastructure.ai;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AnaWebSearchToolTest {

    @Test
    @DisplayName("rejects a blank search query without calling the internet")
    void blankQuery() {
        AnaWebSearchTool tool = new AnaWebSearchTool(RestClient.builder());
        assertEquals("Busca vazia.", tool.searchWeb("  "));
    }
}
