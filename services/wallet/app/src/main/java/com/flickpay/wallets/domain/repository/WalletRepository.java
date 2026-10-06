package com.flickpay.wallets.domain.repository;

import java.util.List;
import java.util.Optional;

import com.flickpay.wallets.domain.entity.Wallet;
import com.flickpay.wallets.domain.enums.WalletStatus;

public interface WalletRepository {
    Wallet save(Wallet wallet);

    Wallet findById(String walletId);

    Optional<Wallet> findByUserId(String userId);

    List<Wallet> findAll();

    List<Wallet> findByStatus(WalletStatus status);
}
