package com.flickpay.wallets.application.service;

import java.util.List;
import java.util.UUID;

import com.flickpay.wallets.domain.entity.Wallet;
import com.flickpay.wallets.domain.enums.WalletStatus;
import com.flickpay.wallets.domain.exception.WalletNotFoundException;
import com.flickpay.wallets.domain.repository.WalletRepository;

public class GetWalletService {
    private final WalletRepository walletRepository;

    public GetWalletService(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    public Wallet getById(UUID walletId) {
        return walletRepository.findById(walletId)
            .orElseThrow(() -> new WalletNotFoundException(walletId));
    }

    public List<Wallet> findByUserId(UUID userId) {
        return walletRepository.findByUserId(userId);
    }

    public List<Wallet> findByStatus(WalletStatus status) {
        return walletRepository.findByStatus(status);
    }

    public List<Wallet> findAll() {
        return walletRepository.findAll();
    }
}
