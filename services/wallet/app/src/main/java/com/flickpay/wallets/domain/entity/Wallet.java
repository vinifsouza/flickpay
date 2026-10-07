package com.flickpay.wallets.domain.entity;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Currency;
import java.util.List;
import java.util.UUID;

import com.flickpay.wallets.domain.enums.WalletStatus;
import com.flickpay.wallets.domain.exception.IllegalWalletStatusChangeException;

public class Wallet {
    private final UUID id;
    private UUID userId;
    private Currency currency;
    private BigDecimal balance;
    private WalletStatus status;
    private final OffsetDateTime createdAt;
    private final OffsetDateTime updatedAt;

    public Wallet(UUID userId, Currency currency, BigDecimal balance, WalletStatus status) {
        this.id = null;
        this.createdAt = null;
        this.updatedAt = null;
        this.balance = balance;
        this.userId = userId;
        this.currency = currency;
        this.status = status;
    }

    private Wallet(
        UUID id,
        UUID userId,
        Currency currency,
        BigDecimal balance,
        WalletStatus status,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
    ) {
        this.id = id;
        this.userId = userId;
        this.currency = currency;
        this.balance = balance;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Wallet rehydrate(
        UUID id,
        UUID userId,
        Currency currency,
        BigDecimal balance,
        WalletStatus status,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
    ) {
        return new Wallet(
            id,
            userId,
            currency,
            balance,
            status,
            createdAt,
            updatedAt
        );
    }

    public UUID getId() {
        return id;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public WalletStatus getStatus() {
        return status;
    }

    public void setStatus(WalletStatus status) {
        this.status = status;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void updateBalance(BigDecimal amount) {
        this.balance = this.balance.add(amount);
    }

    public void updateStatus(WalletStatus newStatus) {
        List<WalletStatus> inactiveStatuses = Arrays.asList(
            WalletStatus.INACTIVE,
            WalletStatus.SUSPENDED,
            WalletStatus.DELETED
        );

        if (inactiveStatuses.contains(newStatus) && !this.hasZeroBalance()) {
            throw new IllegalWalletStatusChangeException(newStatus);
        }

        this.status = newStatus;
    }

    private Boolean hasZeroBalance() {
        return this.balance.compareTo(BigDecimal.ZERO) == 0;
    }
}
