package com.flickpay.wallets.infrastructure.persistence.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.flickpay.wallets.domain.enums.WalletStatus;
import com.flickpay.wallets.infrastructure.persistence.entity.WalletJpaEntity;

public interface WalletJpaRepository extends JpaRepository<WalletJpaEntity, UUID> {
    List<WalletJpaEntity> findByUserId(UUID userId);
    List<WalletJpaEntity> findByStatus(WalletStatus status);
}
