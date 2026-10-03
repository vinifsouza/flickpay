package com.flickpay.users.domain.repository;

import java.util.Optional;
import java.util.UUID;

import com.flickpay.users.domain.entity.User;

public interface UserRepository {
    User save(User user);

    Optional<User> findById(UUID id);

    boolean existsByEmail(String email);
}
