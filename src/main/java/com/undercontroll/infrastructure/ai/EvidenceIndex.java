package com.undercontroll.infrastructure.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.Set;

public class EvidenceIndex {

    private final ObjectMapper objectMapper;
    private final Set<String> tokens = new LinkedHashSet<>();

    public EvidenceIndex(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public void ingest(Object data) {
        if (data == null) {
            return;
        }
        walk(objectMapper.valueToTree(data));
    }

    public boolean contains(Object value) {
        if (value == null) {
            return true;
        }
        return tokens.contains(normalize(value));
    }

    private void walk(JsonNode node) {
        if (node == null || node.isNull()) {
            return;
        }
        if (node.isNumber() || node.isTextual() || node.isBoolean()) {
            tokens.add(normalizeNode(node));
            return;
        }
        if (node.isArray()) {
            node.forEach(this::walk);
            return;
        }
        if (node.isObject()) {
            node.fields().forEachRemaining(entry -> walk(entry.getValue()));
        }
    }

    private static String normalizeNode(JsonNode node) {
        if (node.isNumber()) {
            return new BigDecimal(node.asText()).stripTrailingZeros().toPlainString();
        }
        return node.asText().trim().toLowerCase();
    }

    static String normalize(Object value) {
        if (value instanceof Number number) {
            return new BigDecimal(number.toString()).stripTrailingZeros().toPlainString();
        }
        return value.toString().trim().toLowerCase();
    }
}
