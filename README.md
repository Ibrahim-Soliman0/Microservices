# ITI Jets — Microservices E-Commerce Platform

![Java](https://img.shields.io/badge/Java_17-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot_3.2-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![Spring Cloud](https://img.shields.io/badge/Spring_Cloud-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL_8-4479A1?style=for-the-badge&logo=mysql&logoColor=white)
![RabbitMQ](https://img.shields.io/badge/RabbitMQ-FF6600?style=for-the-badge&logo=rabbitmq&logoColor=white)
![Consul](https://img.shields.io/badge/Consul-F24C53?style=for-the-badge&logo=consul&logoColor=white)
![Prometheus](https://img.shields.io/badge/Prometheus-E6522C?style=for-the-badge&logo=prometheus&logoColor=white)
![Grafana](https://img.shields.io/badge/Grafana-F46800?style=for-the-badge&logo=grafana&logoColor=white)

A production-style microservices architecture for an ordering system. Services are independently deployable, communicate via REST and RabbitMQ, and are discoverable through Consul — with a full observability stack baked in.

> Fork-friendly. Local-first. Just run `docker compose up --build`.

---

## Architecture

```
Browser
   │
   ▼
┌─────────────────────┐
│   gateway-service   │  :8060 — Spring Cloud Gateway (routes via Consul)
└─────────┬───────────┘
          │  lb:// (load-balanced via Consul)
    ┌─────┴──────────────────────────────────┐
    │                                        │
    ▼                                        ▼
┌─────────┐  ┌──────────────┐  ┌──────────────────┐  ┌──────────────────┐
│  oauth2 │  │ order-service│  │inventory-service │  │ invoice-service  │
│  :8085  │  │    :8082     │  │     :8081        │  │     :8084        │
└────┬────┘  └──────┬───────┘  └────────┬─────────┘  └────────▲─────────┘
     │              │                   │                       │
     │         REST │            RabbitMQ (order.invoice exchange)
     ▼              ▼                                           │
┌──────────┐  ┌──────────────┐                          ┌──────┴──────┐
│user-svc  │  │jwt-auth-svc  │                          │  RabbitMQ   │
│  :7070   │  │    :8083     │                          │  :5672/15672│
└──────────┘  └──────────────┘                          └─────────────┘
                                   ┌─────────────────────────────────────┐
                    Consul :8500   │  Prometheus :9090 → Grafana :3000   │
                    (service reg)  │  Loki :3100 ← Alloy :12345         │
                                   └─────────────────────────────────────┘


```

---

## Services

| Service | Port | Stack | Role |
|---|---|---|---|
| `gateway-service` | 8060 | ![Spring Cloud Gateway](https://img.shields.io/badge/Gateway-6DB33F?style=flat&logo=spring&logoColor=white) | Single entry point, routes all traffic via Consul |
| `oauth2` | 8085 | ![OAuth2](https://img.shields.io/badge/OAuth2-4285F4?style=flat&logo=google&logoColor=white) | Google OAuth2 login, session management, JWT coordination |
| `jwt-auth-service` | 8083 | ![JWT](https://img.shields.io/badge/JWT-000000?style=flat&logo=jsonwebtokens&logoColor=white) | Token generation, validation, and refresh lifecycle |
| `user-service` | 7070 | ![JPA](https://img.shields.io/badge/JPA-59666C?style=flat&logo=hibernate&logoColor=white) | User CRUD, token storage |
| `order-service` | 8082 | ![RabbitMQ](https://img.shields.io/badge/RabbitMQ-FF6600?style=flat&logo=rabbitmq&logoColor=white) | Order orchestration — checks inventory, persists orders, publishes events |
| `inventory-service` | 8081 | ![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=flat&logo=mysql&logoColor=white) | Stock check & decrement |
| `invoice-service` | 8084 | ![RabbitMQ](https://img.shields.io/badge/Consumer-FF6600?style=flat&logo=rabbitmq&logoColor=white) | Auto-creates invoices from `OrderEvent` messages |
| `consul` | 8500 | ![Consul](https://img.shields.io/badge/Consul-F24C53?style=flat&logo=consul&logoColor=white) | Service discovery and health checking |
| `rabbitmq` | 5672 / 15672 | ![RabbitMQ](https://img.shields.io/badge/RabbitMQ-FF6600?style=flat&logo=rabbitmq&logoColor=white) | Message broker / Management UI |
| `prometheus` | 9090 | ![Prometheus](https://img.shields.io/badge/Prometheus-E6522C?style=flat&logo=prometheus&logoColor=white) | Metrics collection |
| `grafana` | 3000 | ![Grafana](https://img.shields.io/badge/Grafana-F46800?style=flat&logo=grafana&logoColor=white) | Dashboards (Prometheus + Loki datasources) |
| `loki` | 3100 | ![Loki](https://img.shields.io/badge/Loki-F46800?style=flat&logo=grafana&logoColor=white) | Centralized log aggregation |
| `alloy` | 12345 | ![Alloy](https://img.shields.io/badge/Alloy-F46800?style=flat&logo=grafana&logoColor=white) | Docker log scraper → Loki |

---

## Getting Started

### Prerequisites

![Docker](https://img.shields.io/badge/Docker_required-2496ED?style=flat&logo=docker&logoColor=white)
![Google](https://img.shields.io/badge/Google_OAuth2_credentials_required-4285F4?style=flat&logo=google&logoColor=white)

### 1. Configure Google OAuth2

In Google Cloud Console, add this **Authorized Redirect URI**:

```
http://localhost:8085/login/oauth2/code/google
```

### 2. Set environment variables

Create a `.env` file in the project root:

```bash
GOOGLE_CLIENT_ID=your-google-client-id
GOOGLE_CLIENT_SECRET=your-google-client-secret
```

### 3. Run

```bash
docker compose up --build
```

Open http://localhost:8060 and start ordering.

---

## Gateway Routes

All external traffic enters through the gateway on port **8060**.

| Path Pattern | Target Service |
|---|---|
| `/api/auth/**`, `/login/**`, `/oauth2/**`, `/` | `oauth2` |
| `/api/orders/**` | `order-service` |
| `/api/inventory/**` | `inventory-service` |
| `/api/users/**` | `user-service` |
| `/invoices/**` | `invoice-service` |

---

## API Reference

### Auth Service — `oauth2` ![port](https://img.shields.io/badge/port-8085-blue?style=flat)

| Method | Path | Description |
|---|---|---|
| `GET` | `/login` | Triggers Google OAuth2 login |
| `GET` | `/api/auth/me` | Returns the logged-in user's profile and JWT |
| `POST` | `/api/auth/logout` | Invalidates session and logs out |
| `GET` | `/api/auth/validate` | Validates session (used internally by gateway) |
| `GET` | `/api/auth/health` | Health check |

### JWT Auth Service ![port](https://img.shields.io/badge/port-8083-blue?style=flat)

| Method | Path | Description |
|---|---|---|
| `POST` | `/token/generate` | Generate access + refresh token pair |
| `POST` | `/token/validate` | Validate an access token |
| `GET` | `/token/extract/username` | Extract username from token |
| `GET` | `/token/extract/userId` | Extract user ID from token |
| `POST` | `/token/refresh` | Exchange refresh token for new access token |
| `POST` | `/token/logout` | Revoke a refresh token |

### Order Service ![port](https://img.shields.io/badge/port-8082-blue?style=flat)

| Method | Path | Description |
|---|---|---|
| `POST` | `/api/orders` | Create a new order (checks stock, decrements, publishes event) |
| `GET` | `/api/orders/{id}` | Get order by ID |
| `GET` | `/api/orders/user/{userId}` | Get all orders for a user |
| `GET` | `/api/orders` | Get all orders |

### Inventory Service ![port](https://img.shields.io/badge/port-8081-blue?style=flat)

| Method | Path | Description |
|---|---|---|
| `POST` | `/api/inventory` | Add a new inventory item |
| `GET` | `/api/inventory` | List all inventory items |
| `GET` | `/api/inventory/{productId}` | Get item by product ID |
| `GET` | `/api/inventory/check/{productId}?quantity=N` | Check stock availability |
| `PUT` | `/api/inventory/decrement` | Decrement stock for a list of items |
| `DELETE` | `/api/inventory/{productId}` | Delete an inventory item |

### Invoice Service ![port](https://img.shields.io/badge/port-8084-blue?style=flat)

| Method | Path | Description |
|---|---|---|
| `GET` | `/invoices/{invoiceId}` | Get invoice by ID |
| `GET` | `/invoices/user/{userId}` | Get all invoices for a user |

### User Service ![port](https://img.shields.io/badge/port-7070-blue?style=flat)

| Method | Path | Description |
|---|---|---|
| `POST` | `/api/users` | Create or update a user |
| `GET` | `/api/users/{id}` | Get user by ID |
| `GET` | `/api/users/email/{email}` | Get user by email |
| `GET` | `/api/users` | List all users |
| `PUT` | `/api/users/{id}` | Update user |
| `PUT` | `/api/users/{id}/token` | Save access/refresh token for user |
| `DELETE` | `/api/users/{id}` | Delete user |

---

## Databases

![MySQL](https://img.shields.io/badge/Database--per--service_pattern-4479A1?style=for-the-badge&logo=mysql&logoColor=white)

Each service owns its own MySQL instance — no shared databases.

| Database | Host Port | Owned By |
|---|---|---|
| `order_db` | 3306 | `order-service` |
| `jwt_db` | 3307 | `jwt-auth-service` |
| `userdb` | 3308 | `user-service` |
| `inventorydb` | 3309 | `inventory-service` |
| `invoice_db` | 3310 | `invoice-service` |

> ⚠️ Default credentials are `root` / `123456` — dev only, change before any real deployment.

---

## Messaging

![RabbitMQ](https://img.shields.io/badge/Async_via_RabbitMQ-FF6600?style=for-the-badge&logo=rabbitmq&logoColor=white)

Order events flow asynchronously via RabbitMQ:

```
order-service  ──publishes──▶  exchange: order.invoice
                               routing-key: invoice
                                    │
                                    ▼
                              queue: invoice.queue
                                    │
                                    ▼
                           invoice-service (consumer)
                           → creates Invoice + InvoiceItems
```

The `invoice-service` is idempotent — if an invoice for a given `orderId` already exists, it returns the existing one rather than creating a duplicate.

---

## Observability

![Prometheus](https://img.shields.io/badge/Prometheus-E6522C?style=for-the-badge&logo=prometheus&logoColor=white)
![Grafana](https://img.shields.io/badge/Grafana-F46800?style=for-the-badge&logo=grafana&logoColor=white)
![Loki](https://img.shields.io/badge/Loki-F46800?style=for-the-badge&logo=grafana&logoColor=white)

All Spring Boot services expose `/actuator/prometheus`. Distributed tracing uses Micrometer + Brave with 100% sampling, propagating `traceId`/`spanId` through HTTP headers and RabbitMQ messages.

| Tool | URL | Credentials |
|---|---|---|
| Prometheus | http://localhost:9090 | — |
| Grafana | http://localhost:3000 | `admin` / `admin` |
| Loki | http://localhost:3100 | — |
| Consul UI | http://localhost:8500 | — |
| RabbitMQ UI | http://localhost:15672 | `root` / `root` |

Grafana comes pre-configured with Prometheus and Loki datasources via provisioning.

---

## Environment Variables

| Variable | Required | Default | Used By |
|---|---|---|---|
| `GOOGLE_CLIENT_ID` | ✅ | — | `oauth2` |
| `GOOGLE_CLIENT_SECRET` | ✅ | — | `oauth2` |
| `CONSUL_HOST` | — | `localhost` | all services |
| `CONSUL_PORT` | — | `8500` | all services |
| `MYSQL_HOST` | — | `localhost` | order, invoice, user, jwt, inventory |
| `RABBITMQ_HOST` | — | `localhost` | order, invoice, inventory |

---

## Project Structure

```
.
├── services/
│   └── gateway-service/        # Spring Cloud Gateway
├── oauth2/                     # Google OAuth2 + session management
├── JWT-auth-service/           # JWT token lifecycle
├── user-service/               # User CRUD
├── order-service/              # Order orchestration
├── inventory-service/          # Stock management
├── invoice-service/            # Invoice generation (event-driven)
├── monitoring/
│   ├── prometheus.yml
│   ├── grafana-datasources.yml
│   ├── grafana-dashboards/
│   ├── loki-config.yml
│   └── alloy-config.alloy
└── docker-compose.yml
```

---

## Order Flow (End-to-End)

```
1. User logs in via Google OAuth2 (oauth2 service)
2. oauth2 calls jwt-auth-service to generate a JWT
3. JWT stored in session and user-service
4. User places order → POST /api/orders (through gateway)
5. order-service checks stock → GET /api/inventory/check/{id}
6. If available, order is persisted with status CONFIRMED
7. order-service decrements stock → PUT /api/inventory/decrement
8. order-service publishes OrderEvent to RabbitMQ
9. invoice-service consumes event → creates Invoice + InvoiceItems
```

---

## Notes

- The JWT secret is hardcoded in `application.yaml` for development convenience — rotate it before any real deployment.
- `OrderEventListener` in `inventory-service` is commented out; the service instead uses a synchronous REST decrement flow called directly by `order-service`.
- Google OAuth2 redirect URI must exactly match what's registered in Google Cloud Console.

---

*Feel free to fork and build your own thing — just mention the original. 👋*
