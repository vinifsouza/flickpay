package com.flickpay.users.domain.exception;

public class UserAlreadyInactiveException extends RuntimeException {
    public UserAlreadyInactiveException(String userId) {
        super("User with ID " + userId + " is already inactive.");
    }
}
