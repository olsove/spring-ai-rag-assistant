# AGENTS.md

## Project overview
Spring AI RAG system for technical documentation with ingestion, vector search, and grounded responses.

## Repository workflow
- Repo created in GitHub first
- Developed via IntelliJ
- No nested repositories
- Keep history clean and incremental

## Build tool
- Use `./gradlew` only
- No Maven

## Architecture
- ingest → retrieval → chat → web
- Strict separation of concerns

## Architectural Decision Records (ADR)
- Local folder for ADRs are docs/adr
- Follow the README.md when creating a new ADR
- Use the template in README.md when creating a new ADR
- Always start out with the Proposed status when creating a new ADR

## Features
- Document ingestion (Markdown, text, extendable to PDF/HTML)
- Local chunking and embedding pipeline (using ONNX)
- Vector similarity search
- Context-aware prompt construction
- Grounded responses with citations
- Clean-layered architecture 
- Integration testing with Testcontainers
- Configurable retrieval parameters (top-k, thresholds)

## Project Structure
```text
src/main/java/no/olsove/learn/ai/rag/spring_ai_rag_assistant
├── ingest       # document loading, chunking, embedding
├── retrieval    # vector search and filtering
├── chat         # prompt orchestration
├── web          # REST API
├── model        # DTOs and domain objects
└── config       # configuration
```

## Design Principles
- No hallucinated answers → responses must be grounded
- Separation of concerns → ingestion, retrieval, generation are isolated
- Deterministic prompts → versionable and testable
- Explicit configuration → no hidden magic
- Testability first → especially retrieval logic
- DDD and clean code → For readability and maintainability 
- Privacy & Cost Efficiency → Use local embeddings for data ingestion to save tokens and improve security.

## Testing Philosophy
- **Focused Tests**: Tests must be concise and target specific logic (e.g. chunking strategy, retrieval thresholds).
- **Executable Documentation**: Tests serve as the primary source of truth for developer intent and implementation details.
- **Integration Testing**: Use `@SpringBootTest` and Spring AI's test support to verify the end-to-end RAG flow.
- **Mocking Strategy**: Mock LLM calls in unit tests to ensure deterministic results and avoid API costs during CI.
- **Grounding Verification**: Tests should explicitly verify that citations are present and grounded in the provided context.

## Commands
- `./gradlew build`
- `./gradlew test`
- `./gradlew check`
- `./gradlew bootRun`

## AI rules
- Always return citations
- Never fabricate sources
- Keep prompts deterministic
- Ask me before implementing changes, i want to see the plan before you do it.

## Configuration
Key parameters (configurable):
- chunk size / overlap
- top-k retrieval
- similarity threshold
- model provider (currently Ollama)
- prompt templates

## Future Improvements
- Hybrid search (keyword and vector)
- Re-ranking
- Streaming responses (SSE)
- Evaluation dataset (integrate with eval-lab project)
- Multi-tenant document isolation
- Observability (tracing + metrics)

## Why this project matters
Most AI demos stop at calling an API.
This project demonstrates:
- real system design
- data grounding
- testable AI behaviour
- production-oriented thinking

## Language
- Prefer British English.
- Use programming language keywords do not change them .

## Definition of done
- Tests pass
- Retrieval covered
- README updated
