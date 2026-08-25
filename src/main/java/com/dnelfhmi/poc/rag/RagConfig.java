package com.dnelfhmi.poc.rag;

import org.springframework.ai.document.Document;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.transformers.TransformersEmbeddingModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.ClassPathResource;

import java.util.List;

/**
 * RAG setup: loads a small knowledge doc from the classpath, splits it into
 * chunks, embeds them locally (ONNX model bundled in the jar — no API key,
 * no download), and stores them in an in-memory vector store.
 * All beans are lazy so the app boots fast; the ONNX model loads on first
 * /api/rag call.
 */
@Configuration
public class RagConfig {

    @Bean
    @Lazy
    public EmbeddingModel embeddingModel() {
        // Bundled all-MiniLM-L6-v2 ONNX model + tokenizer — fully offline.
        return new TransformersEmbeddingModel();
    }

    @Bean
    @Lazy
    public VectorStore vectorStore(EmbeddingModel embeddingModel) {
        SimpleVectorStore store = SimpleVectorStore.builder(embeddingModel).build();

        // Load the sample knowledge base from the classpath.
        ClassPathResource resource = new ClassPathResource("docs/company-policy.txt");
        TextReader reader = new TextReader(resource);
        reader.setCharset(java.nio.charset.StandardCharsets.UTF_8);
        List<Document> documents = reader.get();

        // Split into overlapping chunks for better retrieval.
        TokenTextSplitter splitter = new TokenTextSplitter(200, 50, 5, 1000, true);
        List<Document> chunks = splitter.apply(documents);

        // Embed + store.
        store.add(chunks);
        return store;
    }
}
