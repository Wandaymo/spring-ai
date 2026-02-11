package com.wandaymo.springai;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/youtube")
public class Youtube {

    private final ChatClient chatClient;
    @Value("classpath:/prompts/youtube.st")
    private Resource resource;

    public Youtube(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    @GetMapping("/popular")
    public String findPopularYouTubersByGenre(@RequestParam(value = "genre", defaultValue = "tech") String genre) {
        PromptTemplate promptTemplate = new PromptTemplate(resource);
        Prompt prompt = promptTemplate.create(Map.of("genre", genre));
        return chatClient.prompt(prompt).call().content();
    }
}
