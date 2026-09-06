package com.undercontroll.infrastructure.ai;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.undercontroll.infrastructure.config.AnaProperties;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Repository("anaChatMemoryRepository")
public class RedisChatMemoryRepository implements ChatMemoryRepository {

    static final String KEY_PREFIX = "ana:memory:";

    private final StringRedisTemplate redis;
    private final ObjectMapper objectMapper;
    private final AnaProperties anaProperties;

    public RedisChatMemoryRepository(
            StringRedisTemplate redis,
            ObjectMapper objectMapper,
            AnaProperties anaProperties
    ) {
        this.redis = redis;
        this.objectMapper = objectMapper;
        this.anaProperties = anaProperties;
    }

    @Override
    public List<String> findConversationIds() {
        Set<String> keys = redis.keys(KEY_PREFIX + "*");
        if (keys == null || keys.isEmpty()) {
            return List.of();
        }
        return keys.stream()
                .map(key -> key.substring(KEY_PREFIX.length()))
                .toList();
    }

    @Override
    public List<Message> findByConversationId(String conversationId) {
        String json = redis.opsForValue().get(key(conversationId));
        if (json == null || json.isBlank()) {
            return List.of();
        }
        try {
            List<StoredMessage> stored = objectMapper.readValue(json, new TypeReference<>() {
            });
            List<Message> messages = new ArrayList<>();
            for (StoredMessage item : stored) {
                Message mapped = toMessage(item);
                if (mapped != null) {
                    messages.add(mapped);
                }
            }
            return messages;
        } catch (Exception ex) {
            return List.of();
        }
    }

    @Override
    public void saveAll(String conversationId, List<Message> messages) {
        List<StoredMessage> stored = messages == null
                ? List.of()
                : messages.stream()
                .map(RedisChatMemoryRepository::fromMessage)
                .filter(item -> item != null)
                .toList();
        try {
            String json = objectMapper.writeValueAsString(stored);
            String redisKey = key(conversationId);
            redis.opsForValue().set(redisKey, json, Duration.ofHours(anaProperties.getMemoryTtlHours()));
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to persist chat memory", ex);
        }
    }

    @Override
    public void deleteByConversationId(String conversationId) {
        redis.delete(key(conversationId));
    }

    private static String key(String conversationId) {
        return KEY_PREFIX + conversationId;
    }

    private static StoredMessage fromMessage(Message message) {
        if (message == null || message.getMessageType() == null) {
            return null;
        }
        MessageType type = message.getMessageType();
        if (type != MessageType.USER && type != MessageType.ASSISTANT) {
            return null;
        }
        return new StoredMessage(type.name(), message.getText());
    }

    private static Message toMessage(StoredMessage stored) {
        if (stored == null || stored.type() == null) {
            return null;
        }
        String text = stored.text() == null ? "" : stored.text();
        return switch (stored.type()) {
            case "USER" -> new UserMessage(text);
            case "ASSISTANT" -> new AssistantMessage(text);
            default -> null;
        };
    }

    public record StoredMessage(String type, String text) {
    }
}
