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

## Commands
- `./gradlew build`
- `./gradlew test`
- `./gradlew check`
- `./gradlew bootRun`

## AI rules
- Always return citations
- Never fabricate sources
- Keep prompts deterministic

## Definition of done
- Tests pass
- Retrieval covered
- README updated
