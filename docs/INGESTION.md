# Document Ingestion

The ingestion process is the first step in our RAG pipeline. It involves reading documents, splitting them into smaller chunks, converting those chunks into vector embeddings using a local ONNX model, and storing them in a vector store for retrieval.

## How it Works

1.  **Read**: The `IngestionService` uses a `TextReader` to load the content of uploaded files (Markdown, Text, etc.).
2.  **Chunk**: A `TokenTextSplitter` divides the text into manageable pieces (chunks) to ensure they fit within the model's context window and provide better retrieval accuracy.
3.  **Embed**: The `TransformersEmbeddingModel` (local ONNX) converts each text chunk into a high-dimensional vector.
4.  **Store**: These vectors (and their corresponding text content) are stored in the `SimpleVectorStore`.

## Configuration (`application.yaml`)

You can control the ingestion behavior by adjusting parameters in `src/main/resources/application.yaml`.

### RAG Ingestion Parameters

| Property | Description | Default |
| :--- | :--- | :--- |
| `rag.ingestion.chunk-size` | The target size of each text chunk (in tokens). | `1000` |
| `rag.ingestion.chunk-overlap` | The number of tokens to overlap between adjacent chunks. | `100` |

### Local Embedding Model Parameters

| Property | Description | Default |
| :--- | :--- | :--- |
| `spring.ai.model.embedding` | Specifies the embedding model implementation. | `transformers` |
| `spring.ai.embedding.transformer.onnx.modelUri` | URI of the pre-trained ONNX model. | `all-MiniLM-L6-v2` |
| `spring.ai.embedding.transformer.onnx.tokenizerUri` | URI of the model's tokenizer. | `all-MiniLM-L6-v2` |

## Ingestion API

Use the following endpoint to upload documents for ingestion.

### Endpoint: `POST /api/chat/ingest`

Upload a file (e.g., `.md` or `.txt`) using a `multipart/form-data` request.

### Examples using `curl`

#### 1. Ingest a Markdown File
```bash
curl -X POST http://localhost:8080/api/chat/ingest \
  -F "file=@/path/to/your/documentation.md"
```

#### 2. Ingest a Text File
```bash
curl -X POST http://localhost:8080/api/chat/ingest \
  -F "file=@/path/to/your/manual.txt"
```

#### 3. Ingest via URL (if supported by your client)
```bash
# Note: Currently the application expects a MultipartFile upload
curl -X POST http://localhost:8080/api/chat/ingest \
  -F "file=@example.md"
```

## Verifying Ingestion

After ingestion, you can see logs in the application console:
```text
Ingesting resource: documentation.md with chunk-size: 1000 and chunk-overlap: 100
Adding 5 chunks to vector store
Ingestion completed
```

The ingested data will also be saved to `vector-store.json` if configured in `application.yaml`.
