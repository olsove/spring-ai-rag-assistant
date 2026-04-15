# ADR 0004: RAG Configuration Defaults and Tuning

## Status
Approved

## Date
2026-04-15

## Context
The project requires stable, grounded answers from ingested technical documentation while running locally with ONNX embeddings and Ollama chat generation.

Recent testing showed two recurring failure modes:
- Retrieval returning `0` documents for valid questions when thresholds are too strict.
- Answers missing critical details when retrieval context is too broad and prompt generation is too stochastic.
- Ambiguous questions (for example "What are the limits?") selecting only one limit-related fragment instead of returning section-grouped results.

The `application.yaml` defaults directly shape ingestion quality, retrieval recall/precision, and answer determinism. We need a project baseline that prioritises grounded behaviour for technical documentation and can be tuned incrementally.

## Decision
Adopt the following baseline values for local technical-documentation RAG:

```yaml
rag:
  ingestion:
    chunk-size: 400
    chunk-overlap: 80
  retrieval:
    top-k: 4
    similarity-threshold: 0.2

spring:
  ai:
    ollama:
      chat:
        options:
          temperature: 0.1
```

Guidance for tuning from this baseline:
- If retrieval returns too few/no documents, lower `similarity-threshold` (for example `0.2 -> 0.1 -> 0.0`) before increasing `top-k`.
- If answers mix unrelated facts, decrease `top-k` (for example `4 -> 3`) and avoid high temperature.
- If answers miss key details in long sections, increase `chunk-overlap` first; increase `chunk-size` only when overlap is insufficient.
- If broad queries still blend unrelated sections, keep overlap stable and reduce `chunk-size` further in small steps.
- Use `temperature` near `0.0` to improve deterministic, citation-friendly responses for technical content.

## Alternatives Considered
- Keep previous defaults (`chunk-size: 1000`, `chunk-overlap: 100`, `top-k: 8`, `similarity-threshold: 0.0`, `temperature: 0.7`): not chosen because low threshold + high `top-k` increases noisy context, and higher temperature increases paraphrasing/drift.
- Use strict retrieval (`similarity-threshold >= 0.5`): not chosen because testing showed frequent empty retrieval for legitimate queries.
- Use very small chunks (`chunk-size <= 300`): not chosen because semantic continuity degrades for runbooks and configuration sections.

## Consequences
- Positive impact: Better balance between recall and precision for technical Q&A, with fewer empty retrievals and less fact blending.
- Positive impact: Smaller chunk size improves section isolation for ambiguous queries.
- Positive impact: Lower temperature improves consistency and reproducibility during manual and automated tests.
- Negative impact: Some niche queries may still require lowering threshold to `0.0`.
- Tradeoff: Smaller chunks improve targeting but may increase number of vectors and ingestion overhead.
- Follow-up work: Add retrieval quality tests that assert non-empty citations for canonical smoke-test queries.

## Related
- [0001-use-local-llm-with-ollama.md](./0001-use-local-llm-with-ollama.md)
- [0002-local-ingestion-onnx-embeddings.md](./0002-local-ingestion-onnx-embeddings.md)
- [docs/INGESTION.md](../INGESTION.md)
- [docs/USAGE.md](../USAGE.md)
