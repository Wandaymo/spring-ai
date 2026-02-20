package com.wandaymo.springai;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.converter.MapOutputConverter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/books")
public class BooksController {

    private final ChatClient chatClient;
    private final ChatModel chatModel;

    public BooksController(ChatClient.Builder chatClientBuilder, ChatModel chatModel) {
        this.chatClient = chatClientBuilder.build();
        this.chatModel = chatModel;
    }

    @GetMapping("/author/{author}")
    public Map<String, Object> getAuthorsSocialLinks(@PathVariable String author) {
        var message = """
                Generate a list of links for the author {author}. Include the authors name as the key and any social
                network links as the object {format}
                {format}
                """;

        MapOutputConverter mapOutputConverter = new MapOutputConverter();
        String format = mapOutputConverter.getFormat();

        PromptTemplate promptTemplate = new PromptTemplate(message);
        Prompt prompt = promptTemplate.create(Map.of("author", author, "format", format));
        ChatResponse chatResponse = chatClient.prompt(prompt).call().chatResponse();
        assert chatResponse != null;
        return mapOutputConverter.convert(Objects.requireNonNull(chatResponse.getResult().getOutput().getText()));
    }

    @GetMapping("by-author/")
    public Author getBooksByAuthor(@RequestParam(value = "author") String author) {
        var message = """
                Generate a list of books written by the author {author}. If you aren't positive that a book belongs to
                this author please don't include it.
                {format}
                """;

        var beanOutputConverter = new BeanOutputConverter<>(Author.class);
        String format = beanOutputConverter.getFormat();

        Generation generation = chatModel.call(
                PromptTemplate.builder().template(message).variables(Map.of("author", author, "format", format))
                        .build().create()).getResult();
        assert generation.getOutput().getText() != null;
        return beanOutputConverter.convert(generation.getOutput().getText());
    }
}
