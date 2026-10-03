package com.flickpay.users.presentation.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

import com.flickpay.users.domain.enums.UserStatus;

public record UserResponse(
    UUID id,
    String name,
    String email,
    UserStatus status,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt
) {}
