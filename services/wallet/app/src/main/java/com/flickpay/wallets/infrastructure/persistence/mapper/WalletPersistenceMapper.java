package com.flickpay.wallets.infrastructure.persistence.mapper;

import com.flickpay.wallets.domain.entity.Wallet;
import com.flickpay.wallets.infrastructure.persistence.entity.WalletJpaEntity;

public class WalletPersistenceMapper {
    private WalletPersistenceMapper() {
    }

    public static WalletJpaEntity toJpaEntity(Wallet wallet) {
        return new WalletJpaEntity(
            wallet.getId(),
            wallet.getUserId(),
            wallet.getCurrency(),
            wallet.getBalance(),
            wallet.getStatus(),
            wallet.getCreatedAt(),
            wallet.getUpdatedAt()
        );
    }

    public static Wallet toDomainEntity(WalletJpaEntity walletJpaEntity) {
        return new Wallet(
                walletJpaEntity.getId(),
                walletJpaEntity.getUserId(),
                walletJpaEntity.getCurrency(),
                walletJpaEntity.getBalance(),
                walletJpaEntity.getStatus(),
                walletJpaEntity.getCreatedAt(),
                walletJpaEntity.getUpdatedAt()
        );
    }
}
