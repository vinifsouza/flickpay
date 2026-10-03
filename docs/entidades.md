# Entidades do Digital Wallet Platform

Este documento explica as principais entidades do sistema de carteira digital e como elas se relacionam.

---

## Visão geral

```text
User
 │
 └── Wallet
       │
       ├── Transfer
       │      └── LedgerEntry
       │
       ├── Payment
       │      └── LedgerEntry
       │
       └── LedgerEntry
```

As entidades representam diferentes conceitos do domínio financeiro.

| Entidade | Responsabilidade |
|---|---|
| `User` | Representa quem utiliza o sistema |
| `Wallet` | Representa onde o dinheiro está armazenado |
| `Transfer` | Representa uma transferência entre carteiras |
| `Payment` | Representa um pagamento por uma operação ou compra |
| `LedgerEntry` | Representa um lançamento financeiro imutável |
| `Database Transaction` | Garante atomicidade e consistência das operações |

---

# User

Representa a pessoa ou entidade que utiliza a plataforma.

Exemplo:

```text
User
────────────────────
id: 123
name: Vinícius
email: vinicius@email.com
status: ACTIVE
created_at: ...
```

Um usuário pode possuir uma ou mais carteiras:

```text
User
 │
 ├── Wallet A
 └── Wallet B
```

### Responsabilidade

O `User` representa **quem** está utilizando o sistema.

Não deve ser responsável diretamente por:

- Saldo
- Transferências
- Pagamentos
- Movimentações financeiras

Essas responsabilidades pertencem às outras entidades.

---

# Wallet

Representa a carteira financeira de um usuário.

Exemplo:

```text
Wallet
────────────────────
id: wallet-123
user_id: user-123
currency: BRL
balance: 1500.00
status: ACTIVE
```

A `Wallet` responde perguntas como:

- Qual é o saldo atual?
- Quem é o proprietário?
- Qual é a moeda?
- A carteira está ativa?

Exemplo:

```text
User
  │
  ▼
Wallet
  │
  ├── Balance: R$1.500
  ├── Currency: BRL
  └── Status: ACTIVE
```

### Fonte de verdade

O `balance` pode existir na `Wallet` para facilitar consultas, mas o sistema não deve depender apenas dele como fonte de verdade.

O histórico financeiro deve estar registrado no Ledger.

---

# Transfer

Representa uma operação de transferência de dinheiro entre duas carteiras.

Exemplo:

```text
Wallet A
R$1.000

       Transferência
         R$200
            │
            ▼

Wallet B
R$500
```

Uma transferência poderia possuir:

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

### Responsabilidade

A `Transfer` representa a **operação de negócio**.

Ela responde:

- Quem enviou?
- Quem recebeu?
- Quanto foi transferido?
- Qual é o status?
- Quando ocorreu?

### Status

```text
PENDING
PROCESSING
COMPLETED
FAILED
CANCELLED
```

---

# Transfer ≠ LedgerEntry

Uma transferência é uma operação de negócio.

Um `LedgerEntry` é um lançamento financeiro.

Por exemplo:

```text
Transfer #123

Wallet A → Wallet B
R$200
```

Pode gerar:

```text
LedgerEntry #1
Wallet A
DEBIT
-R$200

LedgerEntry #2
Wallet B
CREDIT
+R$200
```

Portanto:

```text
Transfer
   │
   ├── DEBIT  → Wallet A
   │
   └── CREDIT → Wallet B
```

---

# Payment

Representa um pagamento, normalmente associado a uma compra ou cobrança.

Exemplo:

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

Uma entidade `Payment` poderia ser:

```text
Payment
────────────────────────
id: payment-123
payer_wallet_id: wallet-A
merchant_id: merchant-123
amount: 100.00
currency: BRL
status: COMPLETED
created_at: ...
```

### Transfer vs Payment

## Transfer

Representa dinheiro sendo transferido entre carteiras.

```text
Wallet A
   │
   ▼
Wallet B
```

## Payment

Representa dinheiro sendo utilizado para pagar uma operação.

```text
Customer
   │
   ▼
Payment
   │
   ▼
Merchant
```

Internamente, um `Payment` também pode gerar lançamentos no Ledger.

---

# Ledger

O Ledger representa o histórico contábil das movimentações financeiras.

Ele registra o que aconteceu com o dinheiro.

Exemplo:

```text
Wallet A
Saldo inicial: R$1.000
```

Após uma transferência de R$200:

```text
Ledger

Wallet A
DEBIT  R$200

Wallet B
CREDIT R$200
```

Após receber R$500:

```text
Wallet A
CREDIT R$500
```

O histórico pode ser representado como:

```text
Wallet A

+ R$1.000  Initial Balance
- R$200    Transfer
+ R$500    Transfer
---------------------
= R$1.300
```

### Ledger imutável

O Ledger deve ser tratado como um histórico imutável.

Em vez de simplesmente alterar:

```text
balance = balance - 200
```

o sistema registra o evento financeiro:

```text
Wallet A
DEBIT R$200
```

Isso permite:

- Auditoria
- Reconciliação
- Investigação de problemas
- Reconstrução do histórico
- Verificação de consistência

---

# LedgerEntry

Para o projeto, é recomendável utilizar `LedgerEntry` em vez de uma entidade genérica chamada `Transaction`.

Um `LedgerEntry` representa um lançamento individual.

Exemplo:

```text
LedgerEntry
────────────────────────
id: entry-123
wallet_id: wallet-A
type: DEBIT
amount: 200.00
currency: BRL
reference_type: TRANSFER
reference_id: transfer-123
created_at: ...
```

Outro lançamento:

```text
LedgerEntry
────────────────────────
id: entry-124
wallet_id: wallet-B
type: CREDIT
amount: 200.00
currency: BRL
reference_type: TRANSFER
reference_id: transfer-123
created_at: ...
```

Uma única transferência pode gerar múltiplos `LedgerEntry`.

---

# Por que não utilizar Transaction?

O termo `Transaction` pode gerar confusão porque existem dois conceitos completamente diferentes.

## Database Transaction

É um mecanismo do banco de dados:

```text
BEGIN

UPDATE wallet...

INSERT ledger_entry...

COMMIT
```

Seu objetivo é garantir propriedades como atomicidade.

## Financial Transaction

É uma movimentação financeira:

```text
Wallet A
DEBIT
R$200
```

São conceitos diferentes.

Por isso, neste projeto, o termo recomendado para a movimentação financeira é:

```text
LedgerEntry
```

e `Transaction` deve ser utilizado para se referir à **transação do banco de dados** quando necessário.


João Wallet
R$500
```

## 3. Transfer

```text
Transfer #123

FROM: Vinícius Wallet
TO:   João Wallet

Amount: R$200
Status: COMPLETED
```

## 4. Ledger Entries

São criados dois lançamentos:

```text
Wallet Vinícius
DEBIT
R$200
```

```text
Wallet João
CREDIT
R$200
```

## 5. Resultado

```text
Vinícius
R$800

João
R$700
```

---

# Exemplo com Payment

Agora imagine que João compra um produto por R$300.

```text
João
  │
  │ R$300
  ▼
Payment
  │
  ▼
Merchant
```

O `Payment` poderia ser:

```text
Payment #456

payer: João
merchant: Loja
amount: R$300
status: COMPLETED
```

E o Ledger:

```text
João Wallet
DEBIT R$300

Merchant Wallet
CREDIT R$300
```

---

# Modelo de relacionamento

A estrutura recomendada para o projeto:

```text
User
 │
 └──< Wallet
          │
          ├──< Transfer
          │      │
          │      └──< LedgerEntry
          │
          ├──< Payment
          │      │
          │      └──< LedgerEntry
          │
          └──< LedgerEntry
```

Onde:

```text
User
    = Quem é o usuário

Wallet
    = Onde o dinheiro está

Transfer
    = Movimento de dinheiro entre participantes

Payment
    = Pagamento por uma operação ou compra

LedgerEntry
    = Registro contábil imutável do movimento

Database Transaction
    = Mecanismo para garantir atomicidade
```

---

# Princípio fundamental

O sistema deve garantir:

```text
Dinheiro não pode ser criado
Dinheiro não pode desaparecer
Transferências não podem ser duplicadas
Ledger não pode ser alterado
Saldo não pode ficar inconsistente
```
