package com.flickpay.wallets.presentation.mapper;

import java.util.List;
import java.util.stream.Collectors;

import com.flickpay.wallets.domain.entity.Wallet;
import com.flickpay.wallets.presentation.dto.WalletResponse;

public final class WalletResponseMapper {
    public static WalletResponse toResponse(Wallet wallet) {
        return new WalletResponse(
            wallet.getId(),
            wallet.getUserId(),
            wallet.getBalance(),
            wallet.getCurrency(),
            wallet.getStatus(),
            wallet.getCreatedAt(),
            wallet.getUpdatedAt()
        );
    }

    public static List<WalletResponse> toResponseList(List<Wallet> wallets) {
        return wallets.stream()
            .map(WalletResponseMapper::toResponse)
            .collect(Collectors.toList());
    }
}
