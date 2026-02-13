package com.wandaymo.springai;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SimplePromptController {

    private final ChatClient chatClient;

    public SimplePromptController(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    @GetMapping("/")
    public String simple(@RequestParam(value = "message") String message) {
        ChatResponse chatResponse = chatClient.prompt().user(message).call().chatResponse();
        return String.valueOf(chatResponse);
    }
}
