package com.flickpay.wallets.application.service;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import com.flickpay.wallets.application.dto.CreateWalletCommand;
import com.flickpay.wallets.domain.entity.Wallet;
import com.flickpay.wallets.domain.enums.WalletStatus;
import com.flickpay.wallets.domain.repository.WalletRepository;

@Service
public class CreateWalletService {
    private final WalletRepository walletRepository;

    public CreateWalletService(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    public Wallet create(CreateWalletCommand command) {
        Wallet newWallet = new Wallet(
                command.userId(),
                command.currency(),
                BigDecimal.ZERO,
                WalletStatus.ACTIVE
        );

        return walletRepository.save(newWallet);
    }
}
