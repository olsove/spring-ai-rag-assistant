# Atlas Gateway 2.4 - Technical Operations Manual

## 1. Service Overview
Atlas Gateway is an internal edge API service for routing traffic to backend microservices.

- Service name: `atlas-gateway`
- Runtime: Java 21, Spring Boot 3.5.x
- Default HTTP port: `8080`
- Management port: `9090`
- Health endpoint: `GET /actuator/health`
- Metrics endpoint: `GET /actuator/prometheus`

The gateway supports request authentication, rate limiting, request tracing, and protocol translation between external REST clients and internal gRPC services.

## 2. Deployment Topology
Atlas Gateway runs in Kubernetes with three environments.

- `dev`: namespace `atlas-dev`, minimum replicas `1`
- `staging`: namespace `atlas-staging`, minimum replicas `2`
- `prod`: namespace `atlas-prod`, minimum replicas `4`

Ingress is terminated at NGINX, and internal traffic is encrypted using mTLS between services.

## 3. Core Configuration
Primary configuration is loaded from `application.yaml` and environment variables.

### 3.1 Timeouts
- Incoming HTTP read timeout: `15s`
- Outbound backend connect timeout: `2s`
- Outbound backend read timeout: `5s`
- Circuit breaker open-state duration: `30s`

### 3.2 Retry Policy
- Maximum retries per request: `2`
- Retryable status codes: `502`, `503`, `504`
- Backoff strategy: exponential
- Initial backoff: `100ms`
- Maximum backoff: `1200ms`

### 3.3 Rate Limiting
- Default limit: `120 requests/second` per API key
- Burst capacity: `240`
- Rate-limit response code: `429`
- Response header for resets: `X-RateLimit-Reset`

## 4. Authentication and Authorisation
Atlas Gateway validates JWT tokens issued by `auth.internal.example`.

- Required claim: `scope`
- Required scope for admin routes: `gateway.admin`
- Clock skew tolerance: `45s`
- Token cache TTL: `300s`

If token validation fails, the gateway returns:

- `401` for invalid or expired token
- `403` for valid token with missing scope

## 5. API Route Mapping
Route mappings are explicit and versioned.

| External Route | Method | Internal Target | Notes |
| --- | --- | --- | --- |
| `/api/v1/orders` | `GET` | `order-service:9091/ListOrders` | Supports pagination |
| `/api/v1/orders` | `POST` | `order-service:9091/CreateOrder` | Idempotency key recommended |
| `/api/v1/inventory/{sku}` | `GET` | `inventory-service:9092/GetStock` | SKU must be uppercase |
| `/api/v1/payments/refund` | `POST` | `payment-service:9093/RefundPayment` | Requires `gateway.admin` |

## 6. Logging and Traceability
Structured logs are emitted in JSON format.

- Required fields: `timestamp`, `traceId`, `spanId`, `route`, `status`, `latencyMs`
- Log level defaults: `INFO`
- Slow request threshold: `750ms`
- Requests above threshold are logged with event type `SLOW_REQUEST`

Trace propagation headers:

- `traceparent`
- `x-request-id`

## 7. Operational Commands
Use these commands during support operations.

```bash
# Check deployment status
kubectl -n atlas-prod get deploy atlas-gateway

# Tail live logs
kubectl -n atlas-prod logs deploy/atlas-gateway -f --since=10m

# Restart rollout
kubectl -n atlas-prod rollout restart deploy/atlas-gateway

# Verify rollout status
kubectl -n atlas-prod rollout status deploy/atlas-gateway --timeout=120s
```

## 8. Incident Runbook
Follow this order during production incidents.

1. Confirm service health via `/actuator/health`.
2. Check error ratio in metrics (`5xx` and `429`).
3. Inspect last 10 minutes of logs for `SLOW_REQUEST` and auth failures.
4. Validate dependency availability for order, inventory, and payment services.
5. If circuit breaker remains open for more than `2 minutes`, escalate to platform on-call.
6. If rollback is needed, deploy previous image tag and monitor for at least `15 minutes`.

## 9. Error Code Reference
Internal error catalogue used by support teams.

| Error Code | Meaning | Typical Action |
| --- | --- | --- |
| `AGW-1001` | JWT validation failed | Verify issuer, expiry, and clock skew |
| `AGW-2004` | Route mapping not found | Confirm route exists in route table |
| `AGW-3002` | Upstream timeout | Check backend latency and timeout settings |
| `AGW-4009` | Rate limit exceeded | Inspect API key traffic and burst usage |

## 10. Known Limits
- Maximum request body size: `2MB`
- Maximum header size: `16KB`
- Maximum concurrent connections per pod: `2000`
- Recommended pod CPU request: `500m`
- Recommended pod memory request: `768Mi`

## 11. Change History
- `2026-02-10`: Increased default rate limit from `100` to `120` requests/second.
- `2026-03-05`: Added refund route requiring `gateway.admin`.
- `2026-03-28`: Reduced backend read timeout from `8s` to `5s`.
