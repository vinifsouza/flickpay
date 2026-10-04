package com.flickpay.users.application.dto;

public record CreateUserCommand(
    String name,
    String email
) {}
