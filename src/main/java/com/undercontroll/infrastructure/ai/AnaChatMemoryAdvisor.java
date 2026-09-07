package com.undercontroll.infrastructure.ai;

import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;

import java.util.ArrayList;
import java.util.List;

public class AnaChatMemoryAdvisor implements CallAdvisor {

    private final ChatMemory chatMemory;

    public AnaChatMemoryAdvisor(ChatMemory chatMemory) {
        this.chatMemory = chatMemory;
    }

    @Override
    public ChatClientResponse adviseCall(ChatClientRequest request, CallAdvisorChain chain) {
        String conversationId = conversationId(request);
        List<Message> history = chatMemory.get(conversationId).stream()
                .filter(AnaChatMemoryAdvisor::conversational)
                .toList();
        List<Message> current = request.prompt().getInstructions();
        List<Message> systems = current.stream()
                .filter(message -> message.getMessageType() == MessageType.SYSTEM)
                .toList();
        UserMessage userMessage = request.prompt().getUserMessage();

        List<Message> ordered = new ArrayList<>();
        if (!systems.isEmpty()) {
            ordered.add(new SystemMessage(String.join("\n\n", systems.stream().map(Message::getText).toList())));
        }
        ordered.addAll(history);
        if (userMessage != null) {
            ordered.add(userMessage);
        }

        ChatClientRequest next = request.mutate()
                .prompt(new Prompt(ordered, request.prompt().getOptions()))
                .build();
        ChatClientResponse response = chain.nextCall(next);
        if (userMessage != null) {
            chatMemory.add(conversationId, userMessage);
        }
        if (response.chatResponse() != null) {
            List<Message> assistant = response.chatResponse()
                    .getResults()
                    .stream()
                    .map(generation -> (Message) generation.getOutput())
                    .filter(AnaChatMemoryAdvisor::conversational)
                    .toList();
            if (!assistant.isEmpty()) {
                chatMemory.add(conversationId, assistant);
            }
        }
        return response;
    }

    @Override
    public String getName() {
        return "AnaChatMemoryAdvisor";
    }

    @Override
    public int getOrder() {
        return Advisor.DEFAULT_CHAT_MEMORY_PRECEDENCE_ORDER;
    }

    private static String conversationId(ChatClientRequest request) {
        Object raw = request.context().get(ChatMemory.CONVERSATION_ID);
        return raw == null ? ChatMemory.DEFAULT_CONVERSATION_ID : raw.toString();
    }

    private static boolean conversational(Message message) {
        MessageType type = message.getMessageType();
        return type == MessageType.USER || type == MessageType.ASSISTANT;
    }
}
