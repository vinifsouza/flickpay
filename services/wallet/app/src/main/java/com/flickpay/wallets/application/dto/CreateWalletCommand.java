package com.flickpay.wallets.application.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;

public record CreateWalletCommand(
    UUID userId,
    @NotBlank String currency
) {}
