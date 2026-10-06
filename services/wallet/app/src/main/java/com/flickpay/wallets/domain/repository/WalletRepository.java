package com.flickpay.wallets.domain.repository;

import java.util.List;
import java.util.UUID;

import com.flickpay.wallets.domain.entity.Wallet;
import com.flickpay.wallets.domain.enums.WalletStatus;

public interface WalletRepository {
    Wallet save(Wallet wallet);

    Wallet findById(UUID walletId);

    List<Wallet> findByUserId(UUID userId);

    List<Wallet> findAll();

    List<Wallet> findByStatus(WalletStatus status);
}
