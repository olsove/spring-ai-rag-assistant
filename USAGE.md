# USAGE.md

## Overview
The Spring AI RAG Assistant is a technical documentation assistant that uses Retrieval-Augmented Generation (RAG) to provide grounded answers based on ingested documents.

## Prerequisites
- **Java 21** or later.
- **OpenAI API Key**: Set the `OPENAI_API_KEY` environment variable (required for Chat, but not for Embeddings).
- **Gradle**: Use the provided `./gradlew` wrapper.

## Local Embeddings
This application uses **local ONNX embeddings** (all-MiniLM-L6-v2) by default. This means:
- No tokens are used for document ingestion.
- Document chunks are converted to vectors locally on your machine.
- Privacy is improved as your document content is not sent to OpenAI for embedding.
- Only the final chat query and the relevant context are sent to OpenAI for answer generation.

## Running the Application
Start the application using:
```bash
./gradlew bootRun
```
The API will be available at `http://localhost:8080/api/chat`.

## Core Features

### 1. Document Ingestion
Upload text or Markdown files to the vector store. The application chunks and embeds the content for later retrieval.

**Endpoint:** `POST /api/chat/ingest`

**Example using `curl`:**
```bash
curl -X POST http://localhost:8080/api/chat/ingest \
  -F "file=@/path/to/your/documentation.md"
```

### 2. Context-Aware Chat
Ask questions based on the ingested documents. The system retrieves the most relevant chunks and generates a grounded response with citations.

**Endpoint:** `POST /api/chat`

**Example using `curl`:**
```bash
curl -X POST http://localhost:8080/api/chat \
  -H "Content-Type: application/json" \
  -d '{"query": "How do I configure the RAG parameters?"}'
```

**Response format:**
```json
{
  "answer": "You can configure RAG parameters like chunk-size and top-k in the application.yaml file under the 'rag' section.",
  "citations": ["documentation.md"]
}
```

## Configuration
Key parameters can be adjusted in `src/main/resources/application.yaml`:
- `rag.ingestion.chunk-size`: Size of text chunks (default: 1000).
- `rag.ingestion.chunk-overlap`: Overlap between chunks (default: 100).
- `rag.retrieval.top-k`: Number of documents to retrieve (default: 3).
- `rag.retrieval.similarity-threshold`: Minimum similarity score for retrieval (default: 0.5).

## Examples of Use Cases
- **Technical Support**: Ingest your product manual and ask questions about troubleshooting.
- **Developer Documentation**: Load API specs and ask how to use specific endpoints.
- **Onboarding**: Upload internal wiki pages to help new hires find information quickly.
