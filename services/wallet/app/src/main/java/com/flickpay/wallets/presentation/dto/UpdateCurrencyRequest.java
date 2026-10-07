package com.flickpay.wallets.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record UpdateCurrencyRequest(
    @Schema(description = "The new currency for the wallet", example = "USD", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull String currency
) {}
