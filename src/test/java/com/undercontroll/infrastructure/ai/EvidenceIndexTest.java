package com.undercontroll.infrastructure.ai;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

class EvidenceIndexTest {

    @Test
    @DisplayName("indexes nested numbers and strings from tool results")
    void indexesValues() {
        EvidenceIndex index = new EvidenceIndex(new ObjectMapper());
        index.ingest(List.of(Map.of("price_delta_pct", 11.11, "domain_id", "MLB-MICROWAVES")));
        index.ingest(null);
        assertTrue(index.contains(11.11));
        assertTrue(index.contains("MLB-MICROWAVES"));
        assertTrue(index.contains(null));
    }
}
