package com.flickpay.wallets.infrastructure.persistence.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.flickpay.wallets.domain.entity.Wallet;
import com.flickpay.wallets.domain.enums.WalletStatus;
import com.flickpay.wallets.domain.repository.WalletRepository;
import com.flickpay.wallets.infrastructure.persistence.entity.WalletJpaEntity;
import com.flickpay.wallets.infrastructure.persistence.mapper.WalletPersistenceMapper;

@Repository
public class WalletRepositoryAdapter implements WalletRepository {
    private final WalletJpaRepository walletJpaRepository;

    public WalletRepositoryAdapter(WalletJpaRepository walletJpaRepository) {
        this.walletJpaRepository = walletJpaRepository;
    }

    @Override
    public Wallet save(Wallet wallet) {
        WalletJpaEntity walletJpaEntity = WalletPersistenceMapper.toJpaEntity(wallet);
        WalletJpaEntity savedEntity = walletJpaRepository.save(walletJpaEntity);
        return WalletPersistenceMapper.toDomainEntity(savedEntity);
    }

    @Override
    public List<Wallet> findByUserId(UUID userId) {
        List<WalletJpaEntity> walletEntities = walletJpaRepository.findByUserId(userId);
        return walletEntities.stream()
            .map(WalletPersistenceMapper::toDomainEntity)
            .collect(Collectors.toList());
    }

    @Override
    public List<Wallet> findByStatus(WalletStatus status) {
        List<WalletJpaEntity> walletEntities = walletJpaRepository.findByStatus(status);
        return walletEntities.stream()
            .map(WalletPersistenceMapper::toDomainEntity)
            .collect(Collectors.toList());
    }

    @Override
    public Optional<Wallet> findById(UUID walletId) {
        return walletJpaRepository.findById(walletId)
            .map(WalletPersistenceMapper::toDomainEntity);
    }

    @Override
    public List<Wallet> findAll() {
        List<WalletJpaEntity> walletEntities = walletJpaRepository.findAll();
        return walletEntities.stream()
            .map(WalletPersistenceMapper::toDomainEntity)
            .collect(Collectors.toList());
    }
}
