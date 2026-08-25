# Spring AI POC — Spring Boot (Java)

A minimal, compilable **Spring Boot 3.5 + Spring AI 1.0** application demonstrating the core
LLM/GenAI patterns an AI Engineer builds day-to-day:

| Endpoint | What it shows |
|---|---|
| `GET /api/chat?message=hi` | Basic LLM chat (ChatClient) |
| `POST /api/chat` | Chat with optional `system` instruction |
| `GET /api/structured?city=Kuala Lumpur` | **Structured output** — model returns typed JSON record |
| `GET /api/agent?question=What is the weather in Kuala Lumpur?` | **Tool/function calling** — model autonomously calls `@Tool` methods |
| `GET /api/rag?question=What is the leave policy?` | **RAG** — retrieve from vector store → ground answer → cite sources |

## Requirements
- **Java 21** (this host uses a portable Temurin JDK under `/opt/data/toolchains`)
- **Maven 3.9+**
- An API key: `OPENAI_API_KEY` (or `ANTHROPIC_API_KEY`) — export before running.
  - For local/dev without keys, point Spring AI at **Ollama** (see `application.yaml`).

## Run it
```bash
export OPENAI_API_KEY=sk-...
mvn spring-boot:run
```

## Try it
```bash
curl "http://localhost:8080/api/chat?message=Hello"
curl "http://localhost:8080/api/structured?city=Tokyo"
curl "http://localhost:8080/api/agent?question=What is the weather in Kuala Lumpur and today's date?"
curl "http://localhost:8080/api/rag?question=How many annual leave days do employees get?"
```

## Project layout
```
src/main/java/com/dnelfhmi/poc/
├── SpringAiPocApplication.java     # boot entry
├── controller/
│   ├── ChatController.java         # basic + system-prompt chat
│   ├── StructuredOutputController.java
│   ├── AgentController.java        # tool-calling agent
│   └── RagController.java          # RAG with grounding + citations
├── tools/
│   └── WeatherAndDateTools.java    # @Tool methods the LLM can call
├── dto/CityInfo.java               # structured output record
└── rag/RagConfig.java              # SimpleVectorStore + chunking
src/main/resources/
├── application.yaml                # model config (OpenAI/Anthropic/Ollama)
└── docs/company-policy.txt         # sample knowledge base for RAG
```

## Extending into a real agent
- Swap `SimpleVectorStore` for **PGVector / Redis / Qdrant** (Spring AI supports all).
- Add more `@Tool` beans for real integrations (DB, APIs, MCP servers via `spring-ai-starter-model-mcp`).
- Add `spring-ai-starter-vector-store-pgvector` and a real embedding model for production RAG.

Built by Hermes (hosted) on the Daniel tailnet — edit freely in Cursor.
