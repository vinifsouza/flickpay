package com.flickpay.wallets.application.dto;

import java.util.UUID;

public record UpdateCurrencyCommand(
    UUID walletId,
    String currency
) {}
