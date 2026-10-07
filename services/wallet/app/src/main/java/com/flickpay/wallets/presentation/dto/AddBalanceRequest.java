package com.flickpay.wallets.presentation.dto;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record AddBalanceRequest(
    @Schema(
        description = "The amount to be added to the wallet. If negative, it will be subtracted.",
        example = "100.00",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull BigDecimal amount
) {}
