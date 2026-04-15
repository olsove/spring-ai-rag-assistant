# Curl Smoke Test for RAG Flow

Use these commands to test ingestion and grounded chat responses.

## 1. Ingest the sample document

```bash
curl -X POST http://localhost:8080/api/chat/ingest \
  -F "file=@docs/samples/rag-test-manual.md"
```

Expected response:

```text
File ingested successfully: rag-test-manual.md
```

## 2. Ask targeted retrieval questions

```bash
curl -X POST http://localhost:8080/api/chat \
  -H "Content-Type: application/json" \
  -d '{"query":"What are the default and burst rate limits in Atlas Gateway 2.4?"}'
```

```bash
curl -X POST http://localhost:8080/api/chat \
  -H "Content-Type: application/json" \
  -d '{"query":"Which endpoint should I call for service health, and what is the management port?"}'
```

```bash
curl -X POST http://localhost:8080/api/chat \
  -H "Content-Type: application/json" \
  -d '{"query":"What status codes are retryable and how many retries are allowed?"}'
```

```bash
curl -X POST http://localhost:8080/api/chat \
  -H "Content-Type: application/json" \
  -d '{"query":"Which scope is required for refund payments and what happens if it is missing?"}'
```

```bash
curl -X POST http://localhost:8080/api/chat \
  -H "Content-Type: application/json" \
  -d '{"query":"List the incident response steps when the circuit breaker stays open."}'
```

```bash
curl -X POST http://localhost:8080/api/chat \
  -H "Content-Type: application/json" \
  -d '{"query":"What does error code AGW-3002 mean and what action should support take?"}'
```

## 3. Validate output quality

For each response, confirm:

- `answer` contains facts from the ingested manual.
- `citations` includes `rag-test-manual.md`.
- No invented sources are present.
