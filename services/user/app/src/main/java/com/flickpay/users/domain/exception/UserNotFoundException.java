package com.flickpay.users.domain.exception;

import java.util.UUID;


public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(UUID userId) {
        super("User with ID " + userId.toString() + " not found");
    }

    public UserNotFoundException(String identifier) {
        super("User with identifier " + identifier + " not found");
    }
}
