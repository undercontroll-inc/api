package com.undercontroll.application.controller;

import com.undercontroll.application.dto.chat.ChatSuggestionsResponse;
import com.undercontroll.application.dto.chat.SendChatMessageRequest;
import com.undercontroll.application.dto.chat.SendChatMessageResponse;
import com.undercontroll.infrastructure.config.ApiResponseDocumentation.GetApiResponses;
import com.undercontroll.infrastructure.config.ApiResponseDocumentation.PostApiResponses;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Chat", description = "Ana AI, the workshop assistant")
@SecurityRequirement(name = "Bearer Authentication")
@RequestMapping(value = "/v1/api/chats", produces = MediaType.APPLICATION_JSON_VALUE)
public interface ChatApi {

    @Operation(summary = "Send a question to Ana")
    @PostApiResponses
    @PostMapping("/messages")
    ResponseEntity<SendChatMessageResponse> sendMessage(@Valid @RequestBody SendChatMessageRequest message);

    @Operation(summary = "Get quick suggestions (generates and stores them in Redis if they do not exist yet)")
    @GetApiResponses
    @GetMapping("/suggestions")
    ResponseEntity<ChatSuggestionsResponse> getSuggestions();

    @Operation(summary = "Regenerate quick suggestions")
    @PostApiResponses
    @PostMapping("/suggestions")
    ResponseEntity<ChatSuggestionsResponse> refreshSuggestions();
}
