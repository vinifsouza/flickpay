package com.flickpay.users.application.service;

import org.springframework.stereotype.Service;

import com.flickpay.users.application.dto.CreateUserCommand;
import com.flickpay.users.domain.entity.User;
import com.flickpay.users.domain.exception.EmailAlreadyExistsException;
import com.flickpay.users.domain.repository.UserRepository;

@Service
public class CreateUserService {
    private final UserRepository userRepository;

    public CreateUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User create(CreateUserCommand command) {
        if (userRepository.existsByEmail(command.email())) {
            throw new EmailAlreadyExistsException(command.email());
        }

        User user = User.create(
            command.name(),
            command.email()
        );

        return userRepository.save(user);
    }
}
