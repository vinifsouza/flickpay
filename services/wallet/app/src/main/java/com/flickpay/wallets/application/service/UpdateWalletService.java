package com.flickpay.wallets.application.service;

import java.util.UUID;

import com.flickpay.wallets.application.dto.AddBalanceCommand;
import com.flickpay.wallets.application.dto.DeleteWalletCommand;
import com.flickpay.wallets.application.dto.UpdateCurrencyCommand;
import com.flickpay.wallets.application.dto.UpdateWalletStatusCommand;
import com.flickpay.wallets.domain.entity.Wallet;
import com.flickpay.wallets.domain.enums.WalletStatus;
import com.flickpay.wallets.domain.exception.WalletNotFoundException;
import com.flickpay.wallets.domain.repository.WalletRepository;

public class UpdateWalletService {
    private final WalletRepository walletRepository;

    public UpdateWalletService(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    public Wallet addBalance(AddBalanceCommand command) {
        Wallet wallet = findWallet(command.walletId());

        wallet.updateBalance(command.amount());
        return walletRepository.save(wallet);
    }

    public Wallet updateCurrency(UpdateCurrencyCommand command) {
        Wallet wallet = findWallet(command.walletId());

        wallet.setCurrency(command.currency());
        return walletRepository.save(wallet);
    }

    public Wallet updateStatus(UpdateWalletStatusCommand command) {
        Wallet wallet = findWallet(command.walletId());

        wallet.updateStatus(command.status());
        return walletRepository.save(wallet);
    }

    public Wallet deleteWallet(DeleteWalletCommand command) {
        Wallet wallet = findWallet(command.walletId());

        wallet.updateStatus(WalletStatus.DELETED);
        return walletRepository.save(wallet);
    }

    private Wallet findWallet(UUID walletId) {
        return walletRepository.findById(walletId)
            .orElseThrow(() -> new WalletNotFoundException(walletId));
    }
}
