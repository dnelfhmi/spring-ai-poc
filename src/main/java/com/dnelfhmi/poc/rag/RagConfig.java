package com.dnelfhmi.poc.rag;

import org.springframework.ai.document.Document;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.core.io.ClassPathResource;

import java.util.List;

/**
 * RAG setup: loads a small knowledge doc from the classpath, splits it into
 * chunks, embeds them, and stores them in an in-memory vector store.
 */
@Configuration
public class RagConfig {

    @Bean
    @Lazy  // don't embed at startup — only when /api/rag is first called
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
