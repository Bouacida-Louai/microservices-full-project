# 🚀 Spring Boot Microservices Application

A production-ready microservices application built with Spring Boot and Spring Cloud, featuring service discovery, centralized configuration, API gateway, JWT security, circuit breaker, and Docker containerization.

---

## 📐 Architecture

```
                        ┌─────────────────┐
                        │  Config Server  │
                        │   :8888         │
                        └────────┬────────┘
                                 │ serves config
                ┌────────────────┼────────────────┐
                │                │                │
        ┌───────▼──────┐  ┌──────▼──────┐  ┌─────▼────────┐
        │ Eureka Server│  │ API Gateway │  │  Services... │
        │   :8761      │  │   :8181     │  │              │
        └───────┬──────┘  └──────┬──────┘  └──────────────┘
                │                │ JWT Filter
                │ registers      │ routes requests
                │                │
        ┌───────▼────────────────▼───────────────────┐
        │                                            │
┌───────▼──────┐  ┌─────────────┐  ┌───────────────┐  ┌──────────────┐
│ Auth Service │  │User Service │  │Order Service  │  │Product Svc   │
│   :8185      │  │   :8081     │  │   :8182       │  │   :8183      │
│  JWT tokens  │  │   H2 DB     │  │ Feign+CB+H2   │  │   H2 DB      │
└──────────────┘  └─────────────┘  └───────────────┘  └──────────────┘
```

---

## 🛠️ Tech Stack

| Category | Technology |
|----------|-----------|
| Language | Java 21 |
| Framework | Spring Boot 3.4.4 |
| Service Discovery | Spring Cloud Netflix Eureka |
| API Gateway | Spring Cloud Gateway |
| Config Management | Spring Cloud Config Server |
| Inter-service Communication | OpenFeign |
| Circuit Breaker | Resilience4j |
| Security | Spring Security + JWT |
| Database | H2 (in-memory) |
| Containerization | Docker + Docker Compose |
| Build Tool | Maven |

---

## 📦 Services

| Service | Port | Description |
|---------|------|-------------|
| Config Server | 8888 | Centralized configuration for all services |
| Eureka Server | 8761 | Service discovery and registration |
| API Gateway | 8181 | Single entry point, JWT validation, routing |
| Auth Service | 8185 | User authentication, JWT generation |
| User Service | 8081 | User management CRUD |
| Order Service | 8182 | Order management + Feign + Circuit Breaker |
| Product Service | 8183 | Product management CRUD |

---

## ✨ Features

- **Centralized Config** — all service configurations managed in one place
- **Service Discovery** — services auto-register and discover each other via Eureka
- **API Gateway** — single entry point with JWT validation and role-based routing
- **JWT Authentication** — stateless auth with ADMIN and USER roles
- **Feign Client** — declarative REST client for inter-service communication
- **Circuit Breaker** — Resilience4j protects against cascading failures with fallback
- **Exception Handling** — global exception handler with consistent error responses
- **Docker Compose** — one command to run the entire stack

---

## 🔐 Security

### Roles
| Role | Permissions |
|------|-------------|
| ADMIN | Full access to all endpoints |
| USER | Browse products, create and view orders |
| PUBLIC | Login and register only |

### Public endpoints (no token required)
```
POST /auth-service/auth/register
POST /auth-service/auth/login
```

### Protected endpoints (token required)
```
GET  /user-service/api/users
GET  /product-service/api/products
POST /order-service/api/orders
...
```

---

## ⚡ Circuit Breaker

Order Service uses Resilience4j to protect Feign calls:

```
CLOSED  → normal operation, calls go through
  ↓ 50% failure rate over 5 calls
OPEN    → circuit breaks, fallback returns instantly
  ↓ after 10 seconds
HALF-OPEN → 3 test calls allowed
  ↓ success → CLOSED | failure → OPEN
```

Fallback responses when services are down:
```json
{
  "user": { "id": 1, "name": "Unknown User", "email": "N/A" },
  "product": { "id": 1, "name": "Unknown Product", "price": 0.0 }
}
```

---

## 🐳 Running with Docker

### Prerequisites
- Docker Desktop installed and running
- Ports 8081, 8182, 8183, 8185, 8761, 8888, 8181 available

### Start everything
```bash
docker-compose up --build
```

### Stop everything
```bash
docker-compose down
```

### View logs of a specific service
```bash
docker logs user-service
docker logs order-service
```

### Restart a single service
```bash
docker-compose restart user-service
```

---

## 🧪 API Endpoints

### Auth
```
POST /auth-service/auth/register
Body: { "username": "john", "password": "1234", "role": "USER" }

POST /auth-service/auth/login
Body: { "username": "john", "password": "1234" }
Response: { "token": "eyJhbG..." }
```

### Users (requires token)
```
GET  /user-service/api/users
GET  /user-service/api/users/{id}
POST /user-service/api/users
Body: { "name": "John Doe", "email": "john@test.com" }
```

### Products (requires token)
```
GET  /product-service/api/products
GET  /product-service/api/products/{id}
POST /product-service/api/products
Body: { "name": "Laptop", "price": 999.99, "stock": 10 }
```

### Orders (requires token)
```
GET  /order-service/api/orders
GET  /order-service/api/orders/{id}
POST /order-service/api/orders
Body: { "userId": 1, "productId": 1, "quantity": 2 }
```

### Using the token
```
Header: Authorization: Bearer eyJhbG...
```

---

## 🔍 Monitoring & Health

```
# Eureka Dashboard - see all registered services
http://localhost:8761

# Config Server - verify config is served correctly
http://localhost:8888/user-service/default
http://localhost:8888/order-service/default
http://localhost:8888/api-gateway/default

# Circuit Breaker health
http://localhost:8182/actuator/health
```

---

## 📁 Project Structure

```
ms-full-project/
├── config-server/
│   └── src/main/resources/
│       └── configs/
│           ├── eureka-server.yml
│           ├── api-gateway.yml
│           ├── auth-service.yml
│           ├── user-service.yml
│           ├── order-service.yml
│           └── product-service.yml
├── eureka-service/
├── api-gateway/
│   └── GatewayConfig.java (dynamic route locator)
│   └── JwtAuthFilter.java
├── auth-service/
│   └── JWT generation + validation
├── user-service/
│   └── UserController / UserService / UserRepository
├── order-service/
│   └── OrderController / OrderService
│   └── clients/
│       ├── UserClient.java (@CircuitBreaker + fallback)
│       └── ProductClient.java (@CircuitBreaker + fallback)
├── product-service/
│   └── ProductController / ProductService / ProductRepository
└── docker-compose.yml
```

---

## 🗺️ What I learned building this

- Microservices architecture and how services communicate
- How Eureka service discovery works under the hood
- Centralized configuration with Spring Cloud Config
- JWT stateless authentication flow
- Feign declarative REST clients
- Circuit breaker states: CLOSED → OPEN → HALF-OPEN
- Docker networking (container names vs localhost)
- Docker Compose service orchestration and health checks

---

## 🚀 What's next

- [ ] Replace H2 with PostgreSQL
- [ ] Add Kubernetes deployment
- [ ] Add distributed tracing with Zipkin
- [ ] Add CI/CD pipeline with GitHub Actions
- [ ] Add unit and integration tests

---

## 👨‍💻 Author

Built from scratch as a learning project covering the full Spring Cloud microservices ecosystem.