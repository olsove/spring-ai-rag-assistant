# ADR 0001: Use Local LLM with Ollama for Development and Privacy

## Status
Accepted

## Date
2026-04-13

## Context
The application is a Spring AI RAG system designed for technical documentation. Initially, it was configured to use OpenAI for chat completions, requiring an `OPENAI_API_KEY` and incurring per-token costs. 

The following constraints and goals were identified:
- **Cost**: Ongoing development with OpenAI requires API credits or a paid subscription.
- **Privacy**: Technical documentation may contain sensitive or proprietary information that should not be sent to external cloud providers.
- **Dependency**: Dependence on external APIs can lead to latency and downtime during development.
- **Project Goal**: The project already emphasizes a "local-first" approach by using ONNX for local embeddings to improve security and efficiency.

## Decision
We will switch from OpenAI to **Ollama** for local LLM execution. This aligns the chat generation component with the existing local embedding pipeline.

## Alternatives Considered
- **OpenAI**: Requires API keys, costs money, and sends data to the cloud. Rejected due to cost and privacy concerns.
- **Azure OpenAI / AWS Bedrock**: Similar cloud-based trade-offs as OpenAI.
- **Local Llama.cpp / Hugging Face**: While powerful, Ollama provides a simpler abstraction and better integration with Spring AI via the `spring-ai-ollama-spring-boot-starter`.

## Consequences
- **Positive**: Zero cost for development; enhanced data privacy (data stays on-disk); offline development capability; consistency with local embedding strategy.
- **Negative**: Requires local hardware resources (RAM/GPU); users must install Ollama and download models manually.
- **Tradeoffs**: Local models (e.g., Llama 3) may have lower performance or reasoning capabilities compared to GPT-4o, but are sufficient for technical documentation RAG tasks.

## Related
- `AGENTS.md`: Defines privacy and cost efficiency as core design principles.
- `src/main/resources/application.yaml`: Configuration changes to reflect Ollama usage.
