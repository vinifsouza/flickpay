package com.flickpay.users.presentation.mapper;

import com.flickpay.users.domain.entity.User;
import com.flickpay.users.presentation.dto.UserResponse;

public class UserResponseMapper {
    public static UserResponse toResponse(User user) {
        return new UserResponse(
            user.getId(),
            user.getName(),
            user.getEmail(),
            user.getStatus(),
            user.getCreatedAt(),
            user.getUpdatedAt()
        );
    }
}
