package com.wandaymo.springai;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.ListOutputConverter;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
public class SongsController {

    private final ChatClient chatClient;

    public SongsController(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    @GetMapping("/songs")
    public List<String> getSongByArtist(@RequestParam(value = "artist", defaultValue = "Chris Brown") String artist) {
        var message = """
                Give me a list of top 10 songs for the artist {artist}. If you don't know th answer, just say
                "I don't know the answer"
                {format}
                """;

        ListOutputConverter listOutputConverter = new ListOutputConverter(new DefaultConversionService());

        PromptTemplate promptTemplate = new PromptTemplate(message);
        Prompt prompt = promptTemplate.create(Map.of("artist", artist, "format", listOutputConverter.getFormat()));
        ChatResponse chatResponse = chatClient.prompt(prompt).call().chatResponse();
        assert chatResponse != null;
        return listOutputConverter.convert(Objects.requireNonNull(chatResponse.getResult().getOutput().getText()));
    }
}
