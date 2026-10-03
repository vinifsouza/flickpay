package com.flickpay.users.application.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.flickpay.users.domain.entity.User;
import com.flickpay.users.domain.exception.UserNotFoundException;
import com.flickpay.users.domain.repository.UserRepository;

@Service
public class GetUserService {
    private final UserRepository userRepository;

    public GetUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getUserById(UUID userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));
    }

    public List<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }
}
