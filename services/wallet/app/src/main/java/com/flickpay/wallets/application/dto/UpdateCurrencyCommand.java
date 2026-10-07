package com.flickpay.wallets.application.dto;

import java.util.Currency;
import java.util.UUID;

public record UpdateCurrencyCommand(
    UUID walletId,
    Currency currency
) {}
