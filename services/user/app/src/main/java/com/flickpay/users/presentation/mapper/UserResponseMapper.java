package com.flickpay.users.presentation.mapper;

import java.util.List;

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

    public static List<UserResponse> toResponseList(List<User> users) {
        return users.stream()
            .map(UserResponseMapper::toResponse)
            .toList();
    }
}
