# Spring AI POC — Spring Boot (Java)

**Live-verified:** Chat, Structured Output, Tool-Calling Agent on **Groq** (`gpt-oss-120b`).
RAG uses a bundled local ONNX embedding model (`all-MiniLM-L6-v2`) — fully offline, no API key.

A minimal, compilable **Spring Boot 3.5 + Spring AI 1.0** app demonstrating core LLM patterns:

| Endpoint | Pattern | What it proves |
|---|---|---|
| `GET/POST /api/chat` | Basic chat | Prompt engineering, system instructions, ChatClient |
| `GET /api/structured` | Structured output | Model returns typed JSON (record/dto) — guaranteed schema |
| `GET /api/agent` | Tool calling | Model autonomously calls `@Tool` methods (weather, date) — ReAct loop |
| `GET /api/rag` | RAG | Vector store → similarity search → grounded answer with citations |

## Why each endpoint matters for the interview

| Pattern | Interview talking point |
|---|---|
| **Chat** | "I set up ChatClient with system prompts and user params — separates instruction from data." |
| **Structured output** | "Instead of parsing free text, I use `.entity(Record.class)` — the model returns typed JSON. This is how you get reliable, parseable output in production." |
| **Agent (tool calling)** | "The model calls `@Tool` methods like functions. Each method is a tool — description + params. This is the foundation of agentic AI: the model decides what to call, when, and interprets the result." |
| **RAG** | "Ingest → chunk → embed (locally, no API key) → store → retrieve → ground → generate. I cover failure modes: context stuffing, missing retrieval, hallucinated citations. The eval suite catches those." |

## Quick start (on your Mac)

```bash
# Requirements: Java 21 + Maven 3.9+
brew install openjdk@21 maven   # if not installed

git clone https://github.com/dnelfhmi/spring-ai-poc.git
cd spring-ai-poc

export GROQ_API_KEY=gsk_***
mvn spring-boot:run
```

Then test:
```bash
curl "http://localhost:8080/api/chat?message=Hello"
curl "http://localhost:8080/api/structured?city=Kuala+Lumpur"
curl "http://localhost:8080/api/agent?question=What+is+the+weather+in+KL?"
curl "http://localhost:8080/api/rag?question=How+many+annual+leave+days?"
```

## Architecture

```
pom.xml                          Spring Boot 3.5.3 + Spring AI 1.0.0 BOM
src/main/
├── java/com/dnelfhmi/poc/
│   ├── SpringAiPocApplication.java    @SpringBootApplication entry
│   ├── controller/
│   │   ├── ChatController.java        Basic + system-prompt chat
│   │   ├── StructuredOutputController.java
│   │   ├── AgentController.java       Tool-calling (ReAct loop)
│   │   └── RagController.java         RAG with grounding + citations
│   ├── tools/
│   │   └── WeatherAndDateTools.java   @Tool methods (weather, date)
│   ├── dto/CityInfo.java              Java record for structured output
│   └── rag/RagConfig.java             ONNX embedding + SimpleVectorStore
└── resources/
    ├── application.yaml               Model config (Groq/OpenAI/Ollama)
    └── docs/company-policy.txt        Sample knowledge base for RAG
```

## Provider switching

The app uses **Groq** (free tier, OpenAI-compatible) by default. To switch:

```yaml
spring:
  ai:
    openai:
      base-url: https://api.groq.com/openai      # Groq (free)
      api-key: ${GROQ_API_KEY}
      chat:
        options:
          model: openai/gpt-oss-120b

    # OpenAI (needs credits):
    # openai:
    #   api-key: ${OPENAI_API_KEY}
    #   chat:
    #     options:
    #       model: gpt-4o-mini

    # Ollama (local, free):
    # openai:
    #   base-url: http://localhost:11434/v1
    #   api-key: ollama
    #   chat:
    #     options:
    #       model: llama3.2
```

## RAG embedding

RAG uses a bundled ONNX model (`all-MiniLM-L6-v2` from the `spring-ai-transformers` dependency) — no API key, no download. The model loads lazily on first `/api/rag` call. It requires ~384MB free heap. On constrained hosts, use a remote embedding provider (OpenAI, HF) instead.

## License

POC for interview preparation and personal learning. Free to use.
