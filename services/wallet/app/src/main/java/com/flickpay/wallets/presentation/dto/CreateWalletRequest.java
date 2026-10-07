package com.flickpay.wallets.presentation.dto;

import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateWalletRequest(
    @Schema(description = "The ID of the user who owns the wallet", example = "123e4567-e89b-12d3-a456-426614174000")
    @NotNull UUID userId,

    @Schema(description = "The currency of the wallet", example = "USD")
    @NotBlank String currency
) {}
