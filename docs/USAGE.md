# USAGE.md

## Overview
The Spring AI RAG Assistant is a technical documentation assistant that uses Retrieval-Augmented Generation (RAG) to provide grounded answers based on ingested documents.

## Prerequisites
- **Java 21 (Temurin)**.
- **Docker & Docker Compose**: To run the local AI stack.
- **NVIDIA Container Toolkit**: If you want to use your RTX 2070 SUPER.
- **Ollama**: Handled by Docker, but you need to pull the model (see below).
- **Gradle**: Use the provided `./gradlew` wrapper.

## Local AI Stack (Ollama + GPU)
This application is configured for a **fully local pipeline**:
- **Local Embeddings**: Uses ONNX (all-MiniLM-L6-v2) for on-machine vector generation.
- **Local LLM**: Uses Ollama (llama3) for chat generation, with NVIDIA GPU acceleration.
- **Privacy**: No data is sent to external cloud providers.

## Running the Application

### 1. Build the Application
First, build the JAR file:
```bash
./gradlew build -x test
```

### 2. Start the Stack
Launch the Ollama and Application containers:
```bash
docker compose up -d
```

### 3. Pull the Model
On the first run, you must download the `llama3` model into the Ollama container:
```bash
docker exec -it ollama ollama run llama3
```
*(Once the model starts up and you see the prompt, you can type `/exit` to return to your shell.)*

The API will be available at `http://localhost:8080/api/chat`.

## Core Features

### 1. Document Ingestion
Upload text or Markdown files to the vector store. The application chunks and embeds the content for later retrieval.

For detailed information on the ingestion pipeline and configuration, see [docs/INGESTION.md](INGESTION.md).

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
