package com.flickpay.wallets.presentation.dto;

import com.flickpay.wallets.domain.enums.WalletStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record UpdateWalletStatusRequest(
    @Schema(description = "The new status of the wallet", example = "ACTIVE")
    @NotNull WalletStatus status
) {}
