# FlickPay

Educational project to build a digital wallet and payment platform while exploring backend engineering, distributed systems, scalability, consistency, messaging, resilience, and observability.

The project simulates a payment platform where users can have wallets, add funds, make transfers and payments, and track their financial transactions.

> **Educational project only. No real money will be processed.**

---

# Goals

The main goal is to use FlickPay as a laboratory to study:

- API Gateway
- Load Balancing
- Kubernetes
- Horizontal Scaling
- Auto Scaling
- Redis / Caching
- Apache Kafka
- Messaging
- Event-Driven Architecture
- Idempotency
- Concurrency
- Data Consistency
- Distributed Transactions
- Resilience
- Retry / Backoff
- Circuit Breaker
- Dead Letter Queues
- Testing
- Load Testing
- Metrics
- Observability
- Distributed Tracing
- Infrastructure as Code
- Serverless

---

# Stack

| Category | Technology |
|---|---|
| Core Services | Java |
| Framework | Spring Boot |
| Serverless | Go |
| Database | PostgreSQL |
| Cache | Redis |
| Messaging | Apache Kafka |
| API Gateway | AWS API Gateway |
| Load Balancer | AWS ALB |
| Containers | Docker |
| Orchestration | Kubernetes |
| Local Kubernetes | Kind / Minikube |
| Metrics | Prometheus |
| Dashboard | Grafana |
| Tracing | OpenTelemetry |
| Load Testing | k6 |
| Tests | JUnit / Mockito |
| CI/CD | GitHub Actions |
| Infrastructure | Terraform |
| Cloud | AWS |

---

# System Overview

FlickPay will provide digital wallets for users.

A user can:

```text
Create account
    ↓
Create wallet
    ↓
Add funds
    ↓
Transfer money
    ↓
Make payments
    ↓
Receive money
    ↓
Check balance
    ↓
Check transaction history
```

Example:

```text
Wallet A
Balance: R$ 1,000

        Transfer
        R$ 250
           │
           ▼

Wallet B
Balance: R$ 500
```

Result:

```text
Wallet A → R$ 750
Wallet B → R$ 750
```

---

# Architecture

The project will evolve gradually from a monolith into a distributed microservices architecture.

The final architecture will be based on **domain ownership**, with each service owning its own database.

```text
                           Client
                              │
                              ▼
                        API Gateway
                              │
                              ▼
                        Load Balancer
                              │
        ┌─────────────────────┼─────────────────────┐
        ▼                     ▼                     ▼
   User Service         Wallet Service       Payment Service
        │                     │                     │
     user-db              wallet-db            payment-db
                                                    │
        │                     │                     │
        │                     └──────────┬──────────┘
        │                                │
        │                              Kafka
        │                                │
        │                 ┌──────────────┼──────────────┐
        │                 ▼              ▼              ▼
        │          Transfer Service  Ledger Service   Consumers
        │                 │              │
        │           transfer-db       ledger-db
        │
        └───────────────────────────────────────────────

                         Observability
                  ┌─────────────────────────┐
                  │ Prometheus              │
                  │ Grafana                 │
                  │ OpenTelemetry           │
                  └─────────────────────────┘
```

## Microservices

| Service | Responsibility | Database |
|---|---|---|
| **User Service** | Users and account status | `user-db` |
| **Wallet Service** | Wallets, balances, credits and debits | `wallet-db` |
| **Transfer Service** | Wallet-to-wallet transfers | `transfer-db` |
| **Payment Service** | Product/service payments | `payment-db` |
| **Ledger Service** | Financial records and audit trail | `ledger-db` |

### Database ownership

Each service exclusively owns its data.

```text
user-db
└── users

wallet-db
└── wallets

transfer-db
├── transfers
├── transfer_idempotency_keys
└── transfer_outbox_events

payment-db
├── payments
├── payment_idempotency_keys
└── payment_outbox_events

ledger-db
├── financial_transactions
└── ledger_entries
```

There are **no foreign keys between microservice databases**.

Cross-service relationships are represented by UUIDs and resolved through APIs or events.

For example:

```text
wallet.user_id
    ↓
User Service

transfer.source_wallet_id
    ↓
Wallet Service

ledger.reference_id
    ↓
Transfer / Payment Service
```

The **Wallet Service is the only service responsible for modifying wallet balances**.

The **Ledger Service maintains an immutable financial history**.

---

# Languages

## Java

Java will be the primary language for the core services.

The initial monolith will contain:

- Users
- Wallets
- Transfers
- Payments
- Ledger

The domain will later be split into the microservices described above.

Main stack:

```text
Java
Spring Boot
Spring Data
Spring Security
PostgreSQL
JUnit
Mockito
```

The Java services will be used to study:

- Domain modeling
- Transactions
- Concurrency
- Database locking
- Consistency
- Validation
- Error handling
- Testing

---

## Go

Go will be introduced later for serverless and decoupled workloads.

Example:

```text
Kafka Event
    ↓
AWS Lambda
    ↓
Go
```

Possible workloads:

- Notification processing
- Fraud analysis
- Transaction processing
- Reconciliation
- DLQ processing
- Scheduled jobs

The goal is to compare traditional containerized workloads with event-driven and serverless workloads.

---

# Roadmap

## 01. Monolith

Start with a simple application.

```text
Client
  ↓
Spring Boot
  ↓
PostgreSQL
```

Implement:

```http
POST /users
POST /wallets

GET /wallets/:id
GET /wallets/:id/balance

POST /wallets/:id/deposit

POST /transfers
POST /payments

GET /transactions
```

Study:

- Spring Boot
- REST
- PostgreSQL
- Database modeling
- Transactions
- Validation
- Error handling

---

# 02. Ledger

Implement the financial ledger.

Each financial operation should generate balanced entries:

```text
DEBIT
CREDIT
```

Example:

```text
Transfer #123
─────────────────────────────
Wallet A    DEBIT    R$100
Wallet B    CREDIT   R$100
─────────────────────────────
```

The ledger should be immutable.

Guarantee:

```text
Total Debits = Total Credits
```

The ledger becomes the financial audit trail, while the wallet balance is maintained as an optimized representation of the current balance.

---

# 03. Idempotency and Concurrency

Implement:

- Idempotency Keys
- Database Transactions
- Isolation Levels
- Pessimistic Locking
- Optimistic Locking

Idempotency is owned by the service handling the operation:

```text
Transfer Service
└── transfer_idempotency_keys

Payment Service
└── payment_idempotency_keys
```

Example:

```http
POST /transfers
Idempotency-Key: 8f72a91c
```

Retrying the same request must not create a duplicate operation.

---

# 04. Tests

Create multiple levels of tests.

### Unit Tests

```text
WalletService
TransferService
PaymentService
LedgerService
```

### Integration Tests

```text
API
 ↓
PostgreSQL
```

### E2E Tests

```text
Create User
    ↓
Create Wallet
    ↓
Deposit
    ↓
Transfer
    ↓
Check Balance
```

### Concurrency Tests

Execute multiple operations simultaneously and verify that balances and financial records remain consistent.

---

# 05. Redis

Add Redis for read optimization and distributed infrastructure concerns.

Example:

```text
GET /wallets/:id/balance
```

Flow:

```text
Request
   ↓
 Redis
   │
   ├── HIT → Response
   │
   └── MISS
        ↓
     PostgreSQL
        ↓
      Redis
```

Study:

- Cache Aside
- TTL
- Cache Invalidation
- Cache Stampede
- Hot Keys
- Eviction
- Cache Hit Rate
- Distributed Locks
- Rate Limiting

PostgreSQL remains the source of truth for financial data.

---

# 06. Microservices

Split the monolith into domain-oriented services:

```text
User Service
    ↓
user-db

Wallet Service
    ↓
wallet-db

Transfer Service
    ↓
transfer-db

Payment Service
    ↓
payment-db

Ledger Service
    ↓
ledger-db
```

Each service owns its database.

There should be no direct database access between services.

Communication happens through:

```text
REST / APIs
Kafka Events
```

---

# 07. Kafka and Messaging

Introduce event-driven processing.

Example:

```text
Transfer Service
       │
       ▼
     Kafka
       │
       ├──────────────┐
       ▼              ▼
Wallet Service   Ledger Service
       │              │
       ▼              ▼
   wallet-db       ledger-db
```

Possible topics:

```text
user.events
wallet.events
transfer.events
payment.events
ledger.events
```

Example events:

```text
UserCreated

WalletCreated
WalletDebited
WalletCredited

TransferRequested
TransferCompleted
TransferFailed

PaymentRequested
PaymentCompleted
PaymentFailed
```

Study:

- Producers
- Consumers
- Consumer Groups
- Partitions
- Offsets
- Retention
- Ordering
- At-least-once delivery
- Idempotent Consumers
- Consumer Lag

---

# 08. Outbox Pattern

Each service that produces events maintains its own outbox.

Example:

```text
transfer-db
├── transfers
├── transfer_idempotency_keys
└── transfer_outbox_events
```

A transfer and its event are created in the same database transaction:

```text
Database Transaction
        │
        ├── Create Transfer
        └── Create Outbox Event
                    │
                    ▼
              Outbox Publisher
                    │
                    ▼
                  Kafka
```

This prevents the database state and published events from becoming inconsistent.

The same pattern is applied to the Payment Service.

---

# 09. Serverless with Go

Introduce AWS Lambda using Go.

Example:

```text
Kafka Event
    ↓
Lambda
    ↓
Go
    ↓
Process Event
```

Possible workloads:

```text
Payment Event
    ↓
Fraud Lambda

Transaction Event
    ↓
Notification Lambda

Scheduled Event
    ↓
Reconciliation Lambda
```

Study:

- AWS Lambda
- Go
- Cold Starts
- Stateless Execution
- Event-Driven Architecture
- Concurrency
- Retry
- Dead Letter Queues
- EventBridge
- SQS

---

# 10. Resilience

Simulate failures:

```text
Payment Service DOWN
Kafka unavailable
Redis unavailable
Database slow
Consumer crash
Network timeout
Lambda failure
```

Implement:

- Timeout
- Retry
- Exponential Backoff
- Jitter
- Circuit Breaker
- Bulkhead
- Backpressure
- Graceful Degradation

---

# 11. Dead Letter Queue

Messages that cannot be processed after multiple attempts should be sent to a DLQ.

```text
Kafka
  ↓
Consumer
  ↓
Retry
  ↓
Retry
  ↓
Retry
  ↓
DLQ
```

Implement a replay mechanism:

```text
DLQ
 ↓
Replay
 ↓
Kafka
 ↓
Consumer
```

---

# 12. API Gateway

Add AWS API Gateway.

Responsibilities:

- Authentication
- Authorization
- Rate Limiting
- Routing
- Request ID
- Request Validation

Example:

```text
/api/users
     ↓
User Service

/api/wallets
     ↓
Wallet Service

/api/transfers
     ↓
Transfer Service

/api/payments
     ↓
Payment Service
```

---

# 13. Load Balancer

Run multiple instances of the services.

```text
              Load Balancer
             /      |      \
            ▼       ▼       ▼
          API #1  API #2  API #3
```

Study:

- L4 vs L7
- Load Balancing
- Health Checks
- Connection Draining
- Horizontal Scaling

---

# 14. Kubernetes

Containerize the services and run them on Kubernetes.

Implement:

- Pods
- Deployments
- Services
- ConfigMaps
- Secrets
- Ingress
- Liveness Probes
- Readiness Probes
- Resource Requests/Limits

Architecture:

```text
Kubernetes Cluster
       │
       ▼
    Ingress
       │
       ▼
    Service
       │
   ┌───┼───┐
   ▼   ▼   ▼
 Pod  Pod  Pod
```

---

# 15. Auto Scaling

Implement Horizontal Pod Autoscaler.

Example:

```text
Low traffic
    ↓
3 Pods

High traffic
    ↓
10 Pods
```

Measure:

- CPU
- Memory
- RPS
- Latency
- Pod count
- Scaling time

---

# 16. Observability

Add:

```text
Prometheus
Grafana
OpenTelemetry
```

Metrics:

```text
Requests/sec
Error Rate
p50
p95
p99
CPU
Memory
Database Connections
Kafka Lag
Redis Hit Rate
Transfers/sec
Failed Transfers
```

---

# 17. Distributed Tracing

Use OpenTelemetry to trace a complete financial operation across services.

Example:

```text
Transfer Request
       │
       ▼
 API Gateway
       │
       ▼
Transfer Service
       │
       ├── transfer-db
       │
       └── Kafka
             │
             ▼
       Wallet Service
             │
             ├── wallet-db
             │
             └── Kafka
                    │
                    ▼
              Ledger Service
                    │
                    ▼
                 ledger-db
```

The goal is to identify where a distributed operation was delayed or failed.

---

# 18. Load Testing

Use k6 to simulate realistic workloads.

### Scenario 1

```text
1,000 users
100 RPS
```

### Scenario 2

```text
10,000 users
1,000 RPS
```

### Scenario 3

```text
100,000 users
5,000+ RPS
```

Measure:

- Throughput
- Latency
- Error Rate
- CPU
- Memory
- Database Load
- Redis Performance
- Kafka Lag
- Scaling Behavior

---

# 19. AWS + Terraform

After the architecture is working locally, migrate the infrastructure to AWS.

Possible architecture:

```text
                         Internet
                            │
                            ▼
                       API Gateway
                            │
                            ▼
                           ALB
                            │
                            ▼
                        EKS Cluster
                            │
          ┌─────────────────┼─────────────────┐
          ▼                 ▼                 ▼
     User Service      Wallet Service    Payment Service
          │                 │                 │
      user-db           wallet-db        payment-db
                            │
                            ▼
                         Kafka / MSK
                            │
                 ┌──────────┴──────────┐
                 ▼                     ▼
          Transfer Service       Ledger Service
                 │                     │
            transfer-db            ledger-db
```

Infrastructure will be managed using:

```text
Terraform
```

---

# Main Challenge

The main challenge is to simulate a high-concurrency financial operation.

Example:

```text
Wallet A
Balance: R$ 1,000
```

Receives:

```text
100 simultaneous requests
```

attempting to:

```text
Transfer R$100
```

The system must guarantee:

```text
Balance never becomes negative

A transfer cannot be processed twice

Money cannot be created

Money cannot disappear

Events cannot be silently lost

The ledger cannot become inconsistent
```

This scenario will be used to study concurrency, locking, idempotency, consistency, messaging, and resilience.

---

# Roadmap

```text
[ ] 01. Monolith - Java / Spring Boot
[ ] 02. Ledger
[ ] 03. Idempotency
[ ] 04. Concurrency
[ ] 05. Unit / Integration / E2E Tests
[ ] 06. Redis
[ ] 07. Microservices
[ ] 08. Kafka
[ ] 09. Outbox Pattern
[ ] 10. Serverless - Go / AWS Lambda
[ ] 11. Resilience
[ ] 12. Dead Letter Queue
[ ] 13. API Gateway
[ ] 14. Load Balancer
[ ] 15. Kubernetes
[ ] 16. Auto Scaling
[ ] 17. Prometheus + Grafana
[ ] 18. OpenTelemetry
[ ] 19. Load Testing
[ ] 20. AWS
[ ] 21. Terraform
```

---

# Project Principle

The project should not start with a complex architecture.

Each architectural evolution should be driven by a concrete problem:

```text
Problem
   ↓
Measurement
   ↓
Bottleneck / Failure
   ↓
New Solution
   ↓
Test
   ↓
Measurement
   ↓
Trade-offs
```

The goal is not simply to build a distributed system, but to understand **why each architectural decision exists, which problem it solves, and what trade-offs it introduces**.
