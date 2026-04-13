# ADR 0002: Use Local Document Ingestion with ONNX Embeddings

## Status
Accepted

## Date
2026-04-13

## Context
The application is a RAG (Retrieval-Augmented Generation) system for technical documentation. A critical part of the RAG pipeline is document ingestion, which includes chunking documents and generating vector embeddings.

The following requirements and constraints were identified:
- **Privacy**: Technical documentation may contain sensitive or proprietary information. Sending this data to external cloud-based embedding services (like OpenAI) increases the risk of data leaks.
- **Cost**: Cloud-based embedding APIs typically charge per token. Large-scale ingestion of technical docs can become expensive.
- **Latency**: Network calls for embeddings can introduce significant overhead during ingestion.
- **Independence**: The system should be capable of running offline or in restricted environments.

## Decision
We will use a **local ingestion pipeline** for generating vector embeddings. Specifically, we will use the **ONNX (Open Neural Network Exchange)** runtime with a pre-trained model (e.g., `all-MiniLM-L6-v2`) for local inference.

This decision ensures that all document processing remains on-disk and on-machine.

## Alternatives Considered
- **Cloud Embeddings (OpenAI/Azure)**: Fast and easy to set up, but rejected due to recurring costs and privacy concerns.
- **Local Python-based Embedding Services**: Requires a separate service (e.g., FastAPI/Sentence-Transformers) and cross-process communication, increasing complexity.
- **Spring AI with ONNX**: Selected because it provides a native Java-based abstraction for local embeddings via the `spring-ai-transformers` library, offering high performance and low complexity.

## Consequences
- **Positive**: 
  - **Zero Cost**: No per-token charges for embeddings.
  - **Enhanced Privacy**: Document content never leaves the local machine.
  - **Low Latency**: Embeddings are generated locally, avoiding network roundtrips.
  - **Offline Capability**: The ingestion pipeline works without an internet connection (after the initial model download).
- **Negative**:
  - **Model Size**: Requires local storage for the ONNX model files.
  - **Resource Usage**: Embedding generation consumes local CPU/RAM resources.
- **Tradeoffs**: Local models like `all-MiniLM-L6-v2` are smaller and faster but may have slightly lower retrieval accuracy compared to state-of-the-art cloud models like `text-embedding-3-large`.

## Related
- `AGENTS.md`: Defines Privacy & Cost Efficiency as core design principles.
- `src/main/resources/application.yaml`: Configuration for ONNX model and tokenizer URIs.
