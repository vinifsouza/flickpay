package com.flickpay.users.infrastructure.persistence.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

import com.flickpay.users.domain.entity.User;
import com.flickpay.users.domain.exception.EmailAlreadyExistsException;
import com.flickpay.users.domain.repository.UserRepository;
import com.flickpay.users.infrastructure.persistence.mapper.UserPersistenceMapper;

@Repository
public class UserRepositoryAdapter implements UserRepository {
    private final UserJpaRepository userJpaRepository;

    public UserRepositoryAdapter(UserJpaRepository userJpaRepository) {
        this.userJpaRepository = userJpaRepository;
    }

    @Override
    public User save(User user) {
        try {
            return UserPersistenceMapper.toDomainEntity(
                userJpaRepository.saveAndFlush(UserPersistenceMapper.toJpaEntity(user))
            );
        } catch (DataIntegrityViolationException exception) {
            throw new EmailAlreadyExistsException(user.getEmail());
        }
    }

    @Override
    public Optional<User> findById(UUID id) {
        return userJpaRepository.findById(id).map(UserPersistenceMapper::toDomainEntity);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userJpaRepository.existsByEmail(email);
    }

    @Override
    public List<User> findByEmail(String email) {
        return userJpaRepository.findByEmail(email)
            .stream()
            .map(UserPersistenceMapper::toDomainEntity)
            .toList();
    }

    @Override
    public List<User> findAll() {
        return userJpaRepository.findAll().stream()
            .map(UserPersistenceMapper::toDomainEntity)
            .toList();
    }
}
