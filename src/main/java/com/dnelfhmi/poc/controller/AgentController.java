package com.dnelfhmi.poc.controller;

import com.dnelfhmi.poc.tools.WeatherAndDateTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Agent-style endpoint: the LLM can call the registered tools to answer.
 * GET /api/agent?question=What is the weather in Kuala Lumpur today?
 */
@RestController
@RequestMapping("/api/agent")
public class AgentController {

    private final ChatClient chatClient;

    public AgentController(ChatClient.Builder builder) {
        this.chatClient = builder
                .defaultTools(new WeatherAndDateTools())
                .build();
    }

    @GetMapping
    public Map<String, String> agent(@RequestParam String question) {
        String reply = chatClient.prompt()
                .user(question)
                .call()
                .content();
        return Map.of("response", reply);
    }
}
