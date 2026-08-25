package com.dnelfhmi.poc.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * RAG endpoint: retrieves relevant chunks from the vector store and grounds
 * the answer in them, with citations.
 * GET /api/rag?question=What is the leave policy?
 */
@RestController
@RequestMapping("/api/rag")
public class RagController {

    private final ChatClient chatClient;
    private final VectorStore vectorStore;

    public RagController(ChatClient.Builder builder, @Lazy VectorStore vectorStore) {
        this.chatClient = builder.build();
        this.vectorStore = vectorStore;
    }

    @GetMapping
    public Map<String, String> rag(@RequestParam String question) {
        // 1. Retrieve top-k relevant chunks.
        List<Document> matches = vectorStore.similaritySearch(
                SearchRequest.builder().query(question).topK(3).build());

        String context = matches.stream()
                .map(d -> "[source: " + d.getMetadata().getOrDefault("source", "unknown") + "] " + d.getText())
                .collect(Collectors.joining("\n\n"));

        // 2. Ground the answer in the retrieved context.
        String answer = chatClient.prompt()
                .system("You are a helpful assistant. Answer ONLY using the provided context. "
                        + "If the context does not contain the answer, say 'I don't know'. Cite sources inline.")
                .user(u -> u.text("Context:\n{context}\n\nQuestion: {question}")
                        .param("context", context)
                        .param("question", question))
                .call()
                .content();

        return Map.of("answer", answer, "sources", context);
    }
}
