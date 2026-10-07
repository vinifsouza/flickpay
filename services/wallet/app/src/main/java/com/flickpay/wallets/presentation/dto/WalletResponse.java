package com.flickpay.wallets.presentation.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

import com.flickpay.wallets.domain.enums.WalletStatus;

public record WalletResponse(
    UUID id,
    UUID userId,
    BigDecimal balance,
    String currency,
    WalletStatus status,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt
) {}
