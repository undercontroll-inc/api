package com.undercontroll.infrastructure.gateway;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.undercontroll.domain.gateway.AnaSuggestionStore;
import com.undercontroll.infrastructure.config.AnaProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisAnaSuggestionStore implements AnaSuggestionStore {

    static final String KEY_PREFIX = "ana:suggestions:";

    private final StringRedisTemplate redis;
    private final ObjectMapper objectMapper;
    private final AnaProperties anaProperties;

    @Override
    public Optional<List<String>> findByUserId(Integer userId) {
        String json = redis.opsForValue().get(key(userId));
        if (json == null || json.isBlank()) {
            return Optional.empty();
        }
        try {
            List<String> suggestions = objectMapper.readValue(json, new TypeReference<>() {
            });
            if (suggestions == null || suggestions.isEmpty()) {
                return Optional.empty();
            }
            return Optional.of(List.copyOf(suggestions));
        } catch (Exception ex) {
            log.warn("Chat suggestions deserialize failed userId={}", userId, ex);
            return Optional.empty();
        }
    }

    @Override
    public void save(Integer userId, List<String> suggestions) {
        try {
            String json = objectMapper.writeValueAsString(suggestions);
            redis.opsForValue().set(
                    key(userId),
                    json,
                    Duration.ofHours(anaProperties.getSuggestionTtlHours())
            );
        } catch (Exception ex) {
            log.warn("Chat suggestions save failed userId={}", userId, ex);
            throw new IllegalStateException("Failed to persist chat suggestions", ex);
        }
    }

    private static String key(Integer userId) {
        return KEY_PREFIX + userId;
    }
}
