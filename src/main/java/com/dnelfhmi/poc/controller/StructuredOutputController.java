package com.dnelfhmi.poc.controller;

import com.dnelfhmi.poc.dto.CityInfo;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Structured-output endpoint: forces the model to return a typed JSON record.
 * GET /api/structured?city=Kuala Lumpur
 */
@RestController
@RequestMapping("/api/structured")
public class StructuredOutputController {

    private final ChatClient chatClient;

    public StructuredOutputController(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    @GetMapping
    public CityInfo structured(@RequestParam(defaultValue = "Kuala Lumpur") String city) {
        return chatClient.prompt()
                .user(u -> u.text("Give me the country, currency, population and timezone for the city: {city}")
                        .param("city", city))
                .call()
                .entity(CityInfo.class);
    }
}
