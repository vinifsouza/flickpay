package com.flickpay.users.domain.exception;

import java.util.UUID;


public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(UUID userId) {
        super("User with ID " + userId.toString() + " not found");
    }

    public UserNotFoundException(String email) {
        super("User with email " + email + " not found");
    }
}
