package com.flickpay.wallets.application.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record AddBalanceCommand(
    UUID walletId,
    BigDecimal amount
) {}
