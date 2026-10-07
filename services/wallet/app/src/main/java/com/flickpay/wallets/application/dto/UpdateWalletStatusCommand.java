package com.flickpay.wallets.application.dto;

import java.util.UUID;

import com.flickpay.wallets.domain.enums.WalletStatus;

public record UpdateWalletStatusCommand(
    UUID walletId,
    WalletStatus status
) {}
