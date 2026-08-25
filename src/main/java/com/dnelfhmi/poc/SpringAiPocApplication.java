package com.dnelfhmi.poc;

import org.springframework.ai.model.transformers.autoconfigure.TransformersEmbeddingModelAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(exclude = TransformersEmbeddingModelAutoConfiguration.class)
public class SpringAiPocApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringAiPocApplication.class, args);
    }
}
