package com.dnelfhmi.poc.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Basic LLM chat endpoint.
 * GET/POST /api/chat?message=Hello
 */
@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatClient chatClient;

    public ChatController(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    @GetMapping
    public Map<String, String> chat(@RequestParam(defaultValue = "Say hello briefly") String message) {
        String reply = chatClient.prompt()
                .user(message)
                .call()
                .content();
        return Map.of("response", reply);
    }

    @PostMapping
    public Map<String, String> chatPost(@RequestBody Map<String, String> body) {
        String message = body.getOrDefault("message", "Say hello briefly");
        String system = body.getOrDefault("system", "");
        ChatClient.ChatClientRequestSpec spec = chatClient.prompt();
        if (!system.isBlank()) {
            spec = spec.system(system);
        }
        String reply = spec.user(message).call().content();
        return Map.of("response", reply);
    }
}
