package com.undercontroll.application.dto.chat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SendChatMessageRequest(

        @NotBlank
        @Size(max = 500)
        String content

) {
}
