# FlickPay Domain Entities

This document describes the main domain entities of FlickPay and their responsibilities within the microservices architecture.

---

# Overview

The domain is divided into five main bounded contexts:

```text
User Service
    │
    └── User

Wallet Service
    │
    └── Wallet

Transfer Service
    │
    ├── Transfer
    ├── Idempotency Key
    └── Outbox Event

Payment Service
    │
    ├── Payment
    ├── Idempotency Key
    └── Outbox Event

Ledger Service
    │
    ├── Financial Transaction
    └── Ledger Entry
```

Each service owns its own data and database.

Cross-service relationships are represented by UUIDs rather than database foreign keys.

| Entity | Service | Responsibility |
|---|---|---|
| `User` | User Service | Represents a platform user |
| `Wallet` | Wallet Service | Represents a user's wallet and current balance |
| `Transfer` | Transfer Service | Represents a transfer between wallets |
| `Payment` | Payment Service | Represents a payment operation |
| `FinancialTransaction` | Ledger Service | Represents a financial operation recorded by the ledger |
| `LedgerEntry` | Ledger Service | Represents an immutable financial entry |
| `IdempotencyKey` | Transfer / Payment Service | Prevents duplicate operations |
| `OutboxEvent` | Transfer / Payment Service | Reliably publishes domain events |
| `Database Transaction` | Each Service | Guarantees atomicity within a service database |

---

# User

The `User` represents a person or entity using the platform.

Example:

```text
User
────────────────────
id: user-123
name: Vinícius
email: vinicius@email.com
status: ACTIVE
created_at: ...
```

A user can own one or more wallets:

```text
User
 │
 ├── Wallet A
 └── Wallet B
```

### Responsibility

The `User` represents **who** is using the platform.

It is not responsible for:

- Wallet balances
- Transfers
- Payments
- Financial records

Those responsibilities belong to other services.

### Service ownership

```text
User Service
    │
    └── user-db
         └── users
```

Other services may store the user's UUID, but they must not create database foreign keys to `user-db`.

---

# Wallet

The `Wallet` represents a user's financial wallet.

Example:

```text
Wallet
────────────────────
id: wallet-123
user_id: user-123
currency: BRL
balance: 1500.00
status: ACTIVE
```

The `user_id` is a reference to the User Service:

```text
wallet.user_id
      │
      ▼
User Service
```

There is no cross-database foreign key.

### Responsibility

The `Wallet` answers questions such as:

- What is the current balance?
- Who owns the wallet?
- What currency does it use?
- Is the wallet active?

### Service ownership

```text
Wallet Service
    │
    └── wallet-db
         └── wallets
```

The **Wallet Service is the only service responsible for modifying wallet balances**.

---

# Wallet Balance

The `balance` represents the current balance of a wallet and is optimized for fast reads.

Example:

```text
Wallet
────────────────
balance: 1500.00
```

However, the balance should not be treated as the complete financial history.

The Ledger Service maintains the immutable financial records.

Conceptually:

```text
Ledger
   │
   │ financial events
   ▼
Wallet Balance
```

This separation allows the project to explore:

- Event-driven updates
- Eventual consistency
- Reconciliation
- Balance rebuilding
- Financial auditing

---

# Transfer

The `Transfer` represents a business operation that moves money between two wallets.

Example:

```text
Wallet A
R$1,000

       Transfer
        R$200
           │
           ▼

Wallet B
R$500
```

A transfer may contain:

```text
Transfer
────────────────────────────
id: transfer-123
source_wallet_id: wallet-A
destination_wallet_id: wallet-B
amount: 200.00
currency: BRL
status: COMPLETED
created_at: ...
```

The wallet IDs are references to the Wallet Service.

They are not database foreign keys.

### Responsibility

The `Transfer` represents the **business operation**.

It answers:

- Who sent the money?
- Who received the money?
- How much was transferred?
- What is the current status?
- When was the operation created?

### Status

```text
PENDING
PROCESSING
COMPLETED
FAILED
CANCELLED
```

### Service ownership

```text
Transfer Service
    │
    └── transfer-db
         ├── transfers
         ├── transfer_idempotency_keys
         └── transfer_outbox_events
```

---

# Transfer vs Ledger Entry

A `Transfer` is a business operation.

A `LedgerEntry` is a financial accounting entry.

For example:

```text
Transfer #123

Wallet A → Wallet B
R$200
```

The operation can result in:

```text
Ledger Entry #1
Wallet A
DEBIT
-R$200
```

and:

```text
Ledger Entry #2
Wallet B
CREDIT
+R$200
```

Conceptually:

```text
Transfer
   │
   ├── DEBIT  → Wallet A
   │
   └── CREDIT → Wallet B
```

The Transfer Service manages the operation.

The Ledger Service records its financial effect.

---

# Payment

The `Payment` represents a payment operation, typically associated with a product or service.

Example:

```text
Customer
    │
    │ R$100
    ▼
 Payment
    │
    ▼
Merchant
```

A payment may contain:

```text
Payment
────────────────────────
id: payment-123
payer_wallet_id: wallet-A
receiver_wallet_id: wallet-B
amount: 100.00
currency: BRL
status: COMPLETED
description: Product purchase
created_at: ...
```

The wallet IDs are references to the Wallet Service.

### Responsibility

The `Payment` represents the **payment business operation**.

It answers:

- Who paid?
- Who received the payment?
- How much was paid?
- What was the payment for?
- What is its current status?

### Service ownership

```text
Payment Service
    │
    └── payment-db
         ├── payments
         ├── payment_idempotency_keys
         └── payment_outbox_events
```

---

# Transfer vs Payment

Both operations move money, but they represent different business concepts.

## Transfer

Represents money being transferred between participants.

```text
Wallet A
   │
   ▼
Wallet B
```

## Payment

Represents money being used to pay for a product or service.

```text
Customer
   │
   ▼
Payment
   │
   ▼
Receiver
```

Both operations can generate financial records in the Ledger Service.

---

# Financial Transaction

`FinancialTransaction` represents a financial operation recorded by the Ledger Service.

Example:

```text
Financial Transaction
────────────────────────────
id: transaction-123
type: TRANSFER
status: COMPLETED
amount: 200.00
currency: BRL
reference_type: TRANSFER
reference_id: transfer-123
created_at: ...
completed_at: ...
```

The `reference_id` points to an entity owned by another service.

For example:

```text
reference_type: TRANSFER
reference_id: transfer-123
```

or:

```text
reference_type: PAYMENT
reference_id: payment-123
```

This is a logical reference, not a database foreign key.

---

# Ledger Service

The Ledger Service maintains the financial history of the platform.

Its database contains:

```text
ledger-db
├── financial_transactions
└── ledger_entries
```

The Ledger records what happened to the money.

Example:

```text
Wallet A
Initial Balance: R$1,000
```

After a transfer of R$200:

```text
Wallet A
DEBIT  R$200

Wallet B
CREDIT R$200
```

### Ledger as an immutable history

The Ledger should be treated as an append-only financial history.

Instead of modifying historical entries:

```text
balance = balance - 200
```

the system records a financial event:

```text
Wallet A
DEBIT R$200
```

This enables:

- Auditing
- Reconciliation
- Incident investigation
- Historical reconstruction
- Consistency verification

---

# Ledger Entry

A `LedgerEntry` represents an individual financial posting.

Example:

```text
LedgerEntry
────────────────────────
id: entry-123
financial_transaction_id: transaction-123
wallet_id: wallet-A
entry_type: DEBIT
amount: 200.00
currency: BRL
created_at: ...
```

Another entry:

```text
LedgerEntry
────────────────────────
id: entry-124
financial_transaction_id: transaction-123
wallet_id: wallet-B
entry_type: CREDIT
amount: 200.00
currency: BRL
created_at: ...
```

A single financial operation can generate multiple ledger entries.

The Ledger Service guarantees the relationship:

```text
Total Debits = Total Credits
```

for a balanced financial operation.

---

# Why Financial Transaction and Database Transaction Are Different

The term `Transaction` can refer to two completely different concepts.

## Database Transaction

A database transaction is a database mechanism:

```text
BEGIN

UPDATE wallet...

INSERT ledger entry...

COMMIT
```

Its purpose is to guarantee properties such as atomicity within a database.

## Financial Transaction

A financial transaction represents a financial operation:

```text
Transfer
R$200
```

These concepts should not be confused.

In FlickPay:

```text
Database Transaction
    = Database consistency mechanism

FinancialTransaction
    = Financial operation recorded by the Ledger

LedgerEntry
    = Individual financial posting
```

---

# Idempotency Key

An `IdempotencyKey` prevents the same client request from creating multiple financial operations.

Idempotency is owned by the service responsible for the operation.

```text
Transfer Service
    │
    └── transfer_idempotency_keys

Payment Service
    │
    └── payment_idempotency_keys
```

Example:

```http
POST /transfers
Idempotency-Key: 8f72a91c
```

If the same request is received again:

```text
Request #1
   ↓
Transfer created

Request #2
   ↓
Same Idempotency-Key
   ↓
Return previous result
```

The system must not create a second transfer.

---

# Outbox Event

Services that produce domain events use the Outbox Pattern.

For example:

```text
Transfer Service
    │
    └── transfer-db
         ├── transfers
         └── transfer_outbox_events
```

The transfer and its event are persisted in the same database transaction:

```text
Database Transaction
        │
        ├── Create Transfer
        │
        └── Create Outbox Event
                    │
                    ▼
              Outbox Publisher
                    │
                    ▼
                  Kafka
```

This prevents a successful database operation from being committed while its corresponding event is silently lost.

The same pattern is used by the Payment Service.

---

# Example: Transfer Flow

Suppose Vinícius has:

```text
Vinícius Wallet
R$1,000
```

and João has:

```text
João Wallet
R$500
```

## 1. Transfer Request

```text
Transfer #123

FROM: Vinícius Wallet
TO:   João Wallet

Amount: R$200
Status: PENDING
```

The Transfer Service creates the transfer and its Outbox Event.

## 2. Event Processing

```text
Transfer Service
      │
      ▼
    Kafka
      │
      ▼
Wallet Service
```

The Wallet Service processes the debit and credit operations.

## 3. Ledger

The financial operation is recorded by the Ledger Service:

```text
Vinícius Wallet
DEBIT R$200
```

```text
João Wallet
CREDIT R$200
```

## 4. Result

```text
Vinícius
R$800

João
R$700
```

---

# Example: Payment Flow

Suppose João purchases a product for R$300.

```text
João
  │
  │ R$300
  ▼
Payment
  │
  ▼
Receiver
```

The Payment Service creates:

```text
Payment #456

payer: João
receiver: Merchant
amount: R$300
status: COMPLETED
```

The Ledger Service records:

```text
João Wallet
DEBIT R$300

Merchant Wallet
CREDIT R$300
```

---

# Domain Model

The recommended domain structure is:

```text
User Service
    │
    └── User

Wallet Service
    │
    └── Wallet

Transfer Service
    │
    ├── Transfer
    ├── IdempotencyKey
    └── OutboxEvent

Payment Service
    │
    ├── Payment
    ├── IdempotencyKey
    └── OutboxEvent

Ledger Service
    │
    ├── FinancialTransaction
    └── LedgerEntry
```

Cross-service references:

```text
User
  │
  └── user_id → Wallet

Wallet
  │
  ├── source_wallet_id → Transfer
  ├── destination_wallet_id → Transfer
  ├── payer_wallet_id → Payment
  └── receiver_wallet_id → Payment

FinancialTransaction
  │
  └── reference_id → Transfer / Payment

LedgerEntry
  │
  └── wallet_id → Wallet
```

These references are **logical references**, not database foreign keys.

---

# Core Responsibilities

```text
User
    = Who is using the platform

Wallet
    = Current representation of where money is held

Transfer
    = Movement of money between participants

Payment
    = Payment for a product or service

FinancialTransaction
    = Financial operation recorded by the Ledger

LedgerEntry
    = Immutable accounting entry

IdempotencyKey
    = Protection against duplicate requests

OutboxEvent
    = Reliable event publication

Database Transaction
    = Atomicity within a service database
```

---

# Fundamental Principles

FlickPay must guarantee:

```text
Money cannot be created
Money cannot disappear

Transfers cannot be processed twice

The Ledger is append-only

Debits and credits remain balanced

Wallet balances cannot become inconsistent

Domain events must not be silently lost

Each service owns its own data

Cross-service relationships do not use database foreign keys
```
