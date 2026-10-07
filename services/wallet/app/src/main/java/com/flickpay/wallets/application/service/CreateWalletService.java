package com.flickpay.wallets.application.service;

import java.math.BigDecimal;

import com.flickpay.wallets.application.dto.CreateWalletCommand;
import com.flickpay.wallets.domain.entity.Wallet;
import com.flickpay.wallets.domain.enums.WalletStatus;
import com.flickpay.wallets.domain.repository.WalletRepository;

public class CreateWalletService {
    private final WalletRepository walletRepository;

    public CreateWalletService(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    public Wallet createWallet(CreateWalletCommand command) {
        Wallet newWallet = new Wallet(
                command.userId(),
                command.currency(),
                BigDecimal.ZERO,
                WalletStatus.ACTIVE
        );

        return walletRepository.save(newWallet);
    }
}
