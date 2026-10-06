# Microservices Architecture

FlickPay is divided into **domain-oriented microservices**, where each service has a clearly defined responsibility and exclusive ownership of its data.

## Services

| Service | Responsibility | Database |
|---|---|---|
| **User Service** | User registration and account status | `user-db` → `users` |
| **Wallet Service** | Wallets, balances, debits and credits | `wallet-db` → `wallets` |
| **Transfer Service** | Transfers between wallets | `transfer-db` → `transfers`, `transfer_idempotency_keys`, `transfer_outbox_events` |
| **Payment Service** | Product and service payments | `payment-db` → `payments`, `payment_idempotency_keys`, `payment_outbox_events` |
| **Ledger Service** | Financial records and audit trail | `ledger-db` → `financial_transactions`, `ledger_entries` |

## Principles

- Each service has **exclusive ownership** of its database.
- There are no **foreign keys between different service databases**.
- Services communicate through **APIs and Kafka events**.
- `outbox_events` remains in the database of the service that produces the event.
- `idempotency_keys` belongs to the service responsible for the operation.
- The **Wallet Service** is the only service responsible for modifying wallet balances.
- The **Ledger Service** maintains the financial history as **append-only** records.
- The wallet balance is an optimized representation of the current state, while the Ledger maintains the financial history.
- Cross-service relationships are represented by **UUID references**, not database foreign keys.

## Architecture

```text
                           API Gateway
                                │
                                ▼
                         Load Balancer (per service)
                                │
              ┌─────────────────┼─────────────────┐
              ▼                 ▼                 ▼
        User Service      Wallet Service    Payment Service
              │                 │                 │
           user-db          wallet-db        payment-db
                                  │                 │
                                  └────────┬────────┘
                                           │
                                         Kafka
                                           │
                         ┌─────────────────┼─────────────────┐
                         ▼                 ▼                 ▼
                  Transfer Service   Ledger Service      Consumers
                         │                 │
                    transfer-db        ledger-db
```

## Communication

Synchronous communication is used when an immediate response is required:

```text
Client
  │
  ▼
API Gateway
  │
  ▼
Service
  │
  ▼
API / Database
```

Asynchronous communication is used for domain events and decoupled processing:

```text
Service
   │
   ▼
Outbox
   │
   ▼
Kafka
   │
   ├──► Wallet Service
   ├──► Ledger Service
   └──► Other Consumers
```

## Data Ownership

Each database belongs exclusively to one service:

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

A service must never directly access another service's database.

For example:

```text
Wallet Service
      │
      ├── wallet.user_id
      │
      └── User Service
```

The `user_id` is a logical reference to the User Service, not a database foreign key.

## Responsibilities

### User Service

Owns user information and account lifecycle.

```text
user-db
└── users
```

### Wallet Service

Owns wallets and current balances.

```text
wallet-db
└── wallets
```

It is the **only service allowed to modify wallet balances**.

### Transfer Service

Owns wallet-to-wallet transfer operations.

```text
transfer-db
├── transfers
├── transfer_idempotency_keys
└── transfer_outbox_events
```

### Payment Service

Owns payment operations.

```text
payment-db
├── payments
├── payment_idempotency_keys
└── payment_outbox_events
```

### Ledger Service

Owns the financial audit trail.

```text
ledger-db
├── financial_transactions
└── ledger_entries
```

Ledger entries are **append-only** and should not be modified after being recorded.

## Architectural Goals

This architecture provides a laboratory for studying:

- Domain-driven service boundaries
- Database-per-service
- Event-driven architecture
- Eventual consistency
- Concurrency
- Idempotency
- Kafka and asynchronous messaging
- Outbox Pattern
- Distributed transactions
- Resilience
- Horizontal scaling
- Observability

The goal is not to create a microservice for every table, but to establish **meaningful domain boundaries** that allow the distributed-system concepts being studied by FlickPay to emerge naturally.
