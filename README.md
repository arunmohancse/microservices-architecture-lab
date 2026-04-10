# microservices-architecture-lab
Hands-on implementation of microservices architecture using Spring Boot, Docker, Kubernetes, Kafka, and observability tools. Covers authentication, authorization, synchronous & asynchronous communication, and performance optimization.

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 21 + Spring Boot 3.3 |
| Security | Spring Security 6, JWT (RSA-256) |
| Database | PostgreSQL 16, Flyway migrations |
| Messaging | Apache Kafka (KRaft) |
| Caching | Redis |
| Tracing | Micrometer + Zipkin |
| Logging | Logback JSON + ELK Stack |
| Metrics | Prometheus + Grafana |
| Config | Spring Cloud Config Server |
| Containers | Docker, Kubernetes (minikube/kind) |
| Frontend | Angular 17+ (Signals) |

---

## Services

| Service | Port | Responsibility |
|---|---|---|
| api-gateway | 8080 | Routing, JWT filter, rate limiting |
| auth-service | 8081 | JWT issuance, user registration/login |
| product-service | 8082 | Product catalog, inventory |
| order-service | 8083 | Cart, checkout, orders |
| notification-service | 8084 | Kafka event consumer, notifications |

---

## How to Run

### Prerequisites
- Java 21
- Maven 3.9+
- Docker Desktop

---

### Phase 1 — Auth + Product Service (current)

#### Step 1 — Start the database

```bash
docker-compose -f docker-compose.infra.yml up -d
```

This starts a single PostgreSQL container and creates four databases:
`auth_db`, `product_db`, `order_db`, `notification_db`.

Verify it is healthy:
```bash
docker ps
# lab-postgres should show "(healthy)"
```

#### Step 2 — Start auth-service

```bash
cd services/auth-service
mvn spring-boot:run
# Starts on http://localhost:8081
# Flyway runs V1 (users) and V2 (refresh_tokens) on first start
```

#### Step 3 — Start product-service

```bash
cd services/product-service
mvn spring-boot:run
# Starts on http://localhost:8082
# Flyway runs V1 (categories), V2 (products), V3 (inventory) on first start
# Fetches auth-service public key from /.well-known/jwks.json on startup
```

---

### Verify the JWT flow

#### Register a customer
```bash
curl -X POST http://localhost:8081/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"customer@test.com","password":"Test1234!"}'
```

#### Login as the seeded admin
```bash
curl -X POST http://localhost:8081/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@lab.com","password":"Admin1234!"}'
```

Copy the `access_token` from the response.

#### Browse products (public — no token needed)
```bash
curl http://localhost:8082/products
```

#### Get categories (to find a categoryId for product creation)
```bash
curl http://localhost:8082/categories
```

#### Create a product (ADMIN token required)
```bash
curl -X POST http://localhost:8082/products \
  -H "Authorization: Bearer <admin_access_token>" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Mechanical Keyboard",
    "price": 89.99,
    "categoryId": "<id from GET /categories>"
  }'
# Expected: 201 Created
```

#### Confirm RBAC — attempt create with a CUSTOMER token
```bash
curl -X POST http://localhost:8082/products \
  -H "Authorization: Bearer <customer_access_token>" \
  -H "Content-Type: application/json" \
  -d '{"name":"Hack","price":1.00,"categoryId":"..."}'
# Expected: 403 Forbidden
```

#### Inspect the public JWKS (what product-service fetches on startup)
```bash
curl http://localhost:8081/.well-known/jwks.json
```

#### Refresh an access token
```bash
curl -X POST http://localhost:8081/auth/refresh \
  -H "Content-Type: application/json" \
  -d '{"refreshToken":"<refresh_token>"}'
```

---

### Reset the database

If you need a clean slate (e.g. after changing a migration file):

```bash
docker-compose -f docker-compose.infra.yml down -v
docker-compose -f docker-compose.infra.yml up -d
# Then restart both services — Flyway re-runs all migrations
```

---

## Implementation Phases

| Phase | Status | What it adds |
|---|---|---|
| 1 | Done | JWT auth + RBAC (auth-service + product-service) |
| 2 | Pending | Docker + Compose (run everything with one command) |
| 3 | Pending | API Gateway (routing, rate limiting, JWT filter) |
| 4 | Pending | Sync service communication (order-service + OpenFeign + Resilience4j) |
| 5 | Pending | Async events (Kafka + notification-service) |
| 6 | Pending | Distributed tracing (Zipkin) + structured logging (ELK) |
| 7 | Pending | Metrics (Prometheus + Grafana) + Redis caching |
| 8 | Pending | Kubernetes deployment (HPA, Ingress, probes) |
| 9 | Pending | Spring Cloud Config Server |
| 10 | Pending | Production hardening |
| 11-15 | Pending | Angular frontend |
