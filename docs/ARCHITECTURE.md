# Microservices Architecture

O FlickPay será dividido em **microserviços orientados a domínio**, onde cada serviço possui responsabilidade bem definida e é dono exclusivo dos seus dados.

## Serviços

| Serviço | Responsabilidade | Banco |
|---|---|---|
| **User Service** | Usuários, cadastro e status | `user-db` → `users` |
| **Wallet Service** | Carteiras, saldo, débito e crédito | `wallet-db` → `wallets` |
| **Transfer Service** | Transferências entre carteiras | `transfer-db` → `transfers`, `idempotency_keys`, `outbox_events` |
| **Payment Service** | Pagamentos de produtos/serviços | `payment-db` → `payments`, `idempotency_keys`, `outbox_events` |
| **Ledger Service** | Registro financeiro e auditoria | `ledger-db` → `financial_transactions`, `ledger_entries` |

## Princípios

- Cada serviço possui **ownership exclusivo** sobre seu banco.
- Não existem **FKs entre bancos de diferentes serviços**.
- Comunicação entre serviços ocorre via **API** ou **eventos Kafka**.
- `outbox_events` permanece no banco do serviço produtor.
- `idempotency_keys` pertence ao serviço responsável pela operação.
- O `Wallet Service` é o único responsável por alterar o saldo.
- O `Ledger Service` mantém o histórico financeiro como **append-only**.
- O saldo da carteira funciona como uma **projeção otimizada para leitura**, enquanto o ledger mantém o histórico financeiro.

## Comunicação

```text
                    API Gateway
                         │
          ┌──────────────┼──────────────┐
          ▼              ▼              ▼
    User Service   Wallet Service   Payment Service
          │              │              │
       user-db       wallet-db      payment-db
                         │              │
                         └──────┬───────┘
                                ▼
                              Kafka
                                │
                 ┌──────────────┼──────────────┐
                 ▼              ▼              ▼
          Transfer Service  Ledger Service  Consumers
             transfer-db      ledger-db
```

Essa divisão permite explorar **event-driven architecture, consistência eventual, concorrência, idempotência, mensageria, resiliência e escalabilidade horizontal** sem criar um microserviço para cada tabela.
