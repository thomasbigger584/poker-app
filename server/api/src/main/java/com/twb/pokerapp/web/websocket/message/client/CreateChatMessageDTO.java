package com.twb.pokerapp.web.websocket.message.client;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateChatMessageDTO {

    @NotBlank(message = "Chat message cannot be blank")
    @Size(min = 5, max = 50, message = "Chat Message must be between 5 and 50 characters")
    private String message;
}
