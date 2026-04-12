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

## Features
- Document ingestion (Markdown, text, extendable to PDF/HTML)
- Chunking and embedding pipeline
- Vector similarity search
- Context-aware prompt construction
- Grounded responses with citations
- Clean layered architecture 
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

## Commands
- `./gradlew build`
- `./gradlew test`
- `./gradlew check`
- `./gradlew bootRun`

## AI rules
- Always return citations
- Never fabricate sources
- Keep prompts deterministic

## Configuration
Key parameters (configurable):
- chunk size / overlap
- top-k retrieval
- similarity threshold
- model provider
- prompt templates

## Future Improvements
- Hybrid search (keyword + vector)
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
- testable AI behavior
- production-oriented thinking

## Definition of done
- Tests pass
- Retrieval covered
- README updated
