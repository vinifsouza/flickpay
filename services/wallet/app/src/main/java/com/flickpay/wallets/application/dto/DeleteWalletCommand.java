package com.flickpay.wallets.application.dto;

import java.util.UUID;

public record DeleteWalletCommand(
    UUID walletId
) {}
