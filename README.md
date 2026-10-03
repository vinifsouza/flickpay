# Digital Wallet Platform

Projeto de estudo para construir uma plataforma de carteira digital e pagamentos, explorando conceitos de backend, sistemas distribuídos, escalabilidade, consistência, mensageria, resiliência e observabilidade.

O projeto simula uma plataforma de pagamentos onde usuários podem possuir carteiras, adicionar saldo, realizar transferências e acompanhar suas transações.

> Projeto exclusivamente educacional. Não haverá movimentação de dinheiro real.

---

## Objetivos

O principal objetivo é utilizar o projeto como um laboratório para estudar:

- API Gateway
- Load Balancer
- Kubernetes
- Escalabilidade horizontal
- Auto Scaling
- Redis / Cache
- Apache Kafka
- Mensageria
- Event-driven architecture
- Idempotência
- Concorrência
- Consistência de dados
- Transações distribuídas
- Resiliência
- Retry / Backoff
- Circuit Breaker
- Dead Letter Queue
- Testes
- Testes de carga
- Métricas
- Observabilidade
- Distributed Tracing
- Infrastructure as Code
- Serverless

---

## Stack

| Categoria | Tecnologia |
|---|---|
| Monolith / Core Services | Java |
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

# Ideia do sistema

A plataforma permitirá que usuários possuam uma carteira digital.

Cada usuário poderá:

```text
Criar conta
    ↓
Criar carteira
    ↓
Adicionar saldo
    ↓
Transferir dinheiro
    ↓
Receber dinheiro
    ↓
Consultar saldo
    ↓
Consultar extrato
```

Exemplo:

```text
Wallet A
Saldo: R$ 1.000,00

        Transferência
        R$ 250,00
              │
              ▼

Wallet B
Saldo: R$ 500,00
```

Resultado:

```text
Wallet A → R$ 750,00
Wallet B → R$ 750,00
```

---

# Arquitetura

A arquitetura será construída gradualmente.

A arquitetura final deverá se aproximar de:

```text
                         Client
                           │
                           ▼
                    API Gateway
                           │
                           ▼
                    Load Balancer
                           │
              ┌────────────┼────────────┐
              ▼            ▼            ▼
         Wallet Service Payment Service User Service
              │            │            │
              └────────────┼────────────┘
                           │
                    ┌──────┴──────┐
                    ▼             ▼
                PostgreSQL      Redis
                    │
                    ▼
                   Kafka
                    │
          ┌─────────┼─────────┐
          ▼         ▼         ▼
       Ledger    Payment     Fraud
       Consumer  Consumer    Consumer
          │         │         │
          └─────────┼─────────┘
                    │
                    ▼
             Observability
          ┌─────────────────┐
          │ Prometheus      │
          │ Grafana         │
          │ OpenTelemetry   │
          └─────────────────┘
```

---

# Linguagens

## Java

Java será utilizado como linguagem principal do projeto.

O monólito inicialmente concentrará:

- Users
- Wallets
- Transfers
- Payments
- Ledger
- Transactions

Stack principal:

```text
Java
Spring Boot
Spring Data
Spring Security
PostgreSQL
JUnit
Mockito
```

O monólito será utilizado para construir o domínio e estudar conceitos como:

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

Go será utilizado posteriormente para funções serverless e componentes desacoplados.

Exemplos:

```text
Kafka Event
    ↓
AWS Lambda
    ↓
Go
```

Possíveis funções:

- Notification processor
- Fraud analysis
- Transaction processor
- Reconciliation
- DLQ processor
- Scheduled jobs

Objetivo:

Comparar uma aplicação tradicional executando em containers com workloads **event-driven e serverless**.

---

# Roadmap

## 01. Monolith

Criar uma aplicação inicialmente simples.

```text
Client
  ↓
Spring Boot
  ↓
PostgreSQL
```

Implementar:

```http
POST /users
POST /wallets

GET /wallets/:id
GET /wallets/:id/balance

POST /wallets/:id/deposit

POST /transfers

GET /transactions
```

### Estudar

- Spring Boot
- REST
- PostgreSQL
- Database modeling
- Transactions
- Validation
- Error handling

---

# 02. Ledger

Implementar o sistema de Ledger.

Cada transferência deverá gerar registros de:

```text
DEBIT
CREDIT
```

Exemplo:

```text
Transfer #123
─────────────────────────────
Wallet A    DEBIT    R$100
Wallet B    CREDIT   R$100
─────────────────────────────
```

O Ledger deverá ser imutável.

Garantir:

```text
Total Debits = Total Credits
```

---

# 03. Idempotência e Concorrência

Implementar:

- Idempotency Keys
- Database Transactions
- Isolation Levels
- Pessimistic Locking
- Optimistic Locking

Exemplo:

```http
POST /transfers
Idempotency-Key: 8f72a91c
```

A mesma requisição enviada novamente não deve criar uma segunda transferência.

---

# 04. Testes

Criar diferentes níveis de testes.

### Unit Tests

```text
WalletService
TransferService
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

Executar múltiplas transferências simultaneamente e verificar a consistência.

---

# 05. Redis

Adicionar Redis para operações de leitura.

Exemplo:

```text
GET /wallets/:id/balance
```

Fluxo:

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

Estudar:

- Cache Aside
- TTL
- Cache Invalidation
- Cache Stampede
- Hot Keys
- Eviction
- Cache Hit Rate

O PostgreSQL continuará sendo a fonte de verdade para informações financeiras.

---

# 06. Kafka e Mensageria

Transformar partes do processamento em uma arquitetura orientada a eventos.

```text
Transfer API
     ↓
   Kafka
     ↓
┌────┼──────────┐
▼    ▼          ▼
Ledger Payment  Fraud
```

Criar topics como:

```text
wallet.created
transfer.created
transfer.completed
transfer.failed
payment.created
payment.completed
payment.failed
```

Estudar:

- Producers
- Consumers
- Consumer Groups
- Partitions
- Offsets
- Retention
- Ordering
- At-least-once delivery
- Idempotent Consumers

---

# 07. Outbox Pattern

Garantir que alterações no banco e publicação de eventos não fiquem inconsistentes.

Problema:

```text
Database
   ↓
Transfer saved

Kafka
   ↓
ERROR
```

Com Outbox:

```text
Database Transaction
        │
        ├── Update Wallet
        ├── Create Ledger Entry
        └── Create Outbox Event
                     │
                     ▼
                Outbox Worker
                     │
                     ▼
                   Kafka
```

---

# 08. Serverless com Go

Introduzir AWS Lambda utilizando Go.

Exemplo:

```text
Kafka Event
    ↓
Lambda
    ↓
Go
    ↓
Process Event
```

Possíveis workloads:

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

Estudar:

- AWS Lambda
- Go
- Cold Start
- Stateless execution
- Event-driven architecture
- Concurrency
- Retry
- Dead Letter Queue
- AWS EventBridge
- SQS

---

# 09. Resiliência

Simular falhas:

```text
Payment Service DOWN
Kafka unavailable
Redis unavailable
Database slow
Consumer crash
Network timeout
Lambda failure
```

Implementar:

- Timeout
- Retry
- Exponential Backoff
- Jitter
- Circuit Breaker
- Bulkhead
- Backpressure
- Graceful Degradation

---

# 10. Dead Letter Queue

Mensagens que não puderem ser processadas após várias tentativas deverão ir para uma DLQ.

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

Criar mecanismo para:

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

# 11. API Gateway

Adicionar AWS API Gateway.

Responsabilidades:

- Authentication
- Authorization
- Rate Limiting
- Routing
- Request ID
- Request Validation

Exemplo:

```text
/api/users
      ↓
User Service

/api/wallets
      ↓
Wallet Service

/api/payments
      ↓
Payment Service
```

---

# 12. Load Balancer

Executar múltiplas instâncias dos serviços.

```text
              Load Balancer
             /      |      \
            ▼       ▼       ▼
          API #1  API #2  API #3
```

Estudar:

- L4 vs L7
- Round Robin
- Health Checks
- Connection Draining
- Horizontal Scaling

---

# 13. Kubernetes

Containerizar os serviços e executar no Kubernetes.

Implementar:

- Pods
- Deployments
- Services
- ConfigMaps
- Secrets
- Ingress
- Liveness Probes
- Readiness Probes
- Resource Requests/Limits

Arquitetura:

```text
Kubernetes Cluster
       │
       ▼
    Ingress
       │
       ▼
    Service
       │
 ┌─────┼─────┐
 ▼     ▼     ▼
Pod   Pod   Pod
```

---

# 14. Auto Scaling

Implementar Horizontal Pod Autoscaler.

Exemplo:

```text
Low traffic
    ↓
3 Pods

High traffic
    ↓
10 Pods
```

Realizar testes de carga e observar:

- CPU
- Memory
- RPS
- Latency
- Pod count
- Scaling time

---

# 15. Observabilidade

Adicionar:

```text
Prometheus
Grafana
OpenTelemetry
```

Métricas:

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

# 16. Distributed Tracing

Utilizar OpenTelemetry para acompanhar uma operação completa:

```text
Transfer Request
       │
       ▼
 API Gateway
       │
       ▼
 Wallet Service
       │
       ├── PostgreSQL
       │
       └── Kafka
              │
              ▼
        Payment Consumer
              │
              ▼
           Ledger
```

Objetivo:

Identificar onde uma transferência demorou ou falhou.

---

# 17. Load Testing

Utilizar k6 para simular cenários reais.

### Cenário 1

```text
1.000 usuários
100 RPS
```

### Cenário 2

```text
10.000 usuários
1.000 RPS
```

### Cenário 3

```text
100.000 usuários
5.000+ RPS
```

Medir:

- Throughput
- Latência
- Error Rate
- CPU
- Memory
- Database Load
- Redis Performance
- Kafka Lag
- Scaling Behavior

---

# 18. AWS + Terraform

Depois que a arquitetura estiver funcionando localmente, migrar os componentes para AWS.

Possível arquitetura:

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
            ┌──────────┼──────────┐
            ▼          ▼          ▼
         Wallet      Payment     User
         Service     Service    Service
            │          │          │
            └──────────┼──────────┘
                       │
              ┌────────┴────────┐
              ▼                 ▼
           RDS PostgreSQL      Redis
                               ElastiCache
              │
              ▼
          Kafka / MSK
```

Infraestrutura gerenciada utilizando:

```text
Terraform
```

---

# Cenário principal

O principal desafio do projeto será simular uma situação de alta concorrência.

```text
Wallet A
Saldo: R$ 1.000
```

Recebe:

```text
100 requisições simultâneas
```

Tentando:

```text
Transferir R$100
```

O sistema deve garantir:

```text
Saldo nunca < R$0
```

e:

```text
Transferência não pode ser processada duas vezes
```

Além disso:

```text
Dinheiro não pode ser criado
Dinheiro não pode desaparecer
Eventos não podem ser perdidos
Ledger não pode ficar inconsistente
```

---

# Roadmap

```text
[ ] 01. Monolith - Java / Spring Boot
[ ] 02. Ledger
[ ] 03. Idempotência
[ ] 04. Concorrência
[ ] 05. Unit / Integration / E2E Tests
[ ] 06. Redis
[ ] 07. Kafka
[ ] 08. Outbox Pattern
[ ] 09. Serverless - Go / AWS Lambda
[ ] 10. Resiliência
[ ] 11. Dead Letter Queue
[ ] 12. API Gateway
[ ] 13. Load Balancer
[ ] 14. Kubernetes
[ ] 15. Auto Scaling
[ ] 16. Prometheus + Grafana
[ ] 17. OpenTelemetry
[ ] 18. Load Testing
[ ] 19. AWS
[ ] 20. Terraform
```

---

# Princípio do projeto

O projeto não deve começar com uma arquitetura complexa.

A evolução deve seguir:

```text
Problema
   ↓
Medição
   ↓
Gargalo / Falha
   ↓
Nova solução
   ↓
Teste
   ↓
Medição
   ↓
Trade-offs
```
