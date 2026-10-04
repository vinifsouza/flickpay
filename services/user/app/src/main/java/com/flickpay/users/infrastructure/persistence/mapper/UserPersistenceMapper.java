package com.flickpay.users.infrastructure.persistence.mapper;

import java.util.List;

import com.flickpay.users.domain.entity.User;
import com.flickpay.users.infrastructure.persistence.entity.UserJpaEntity;

public class UserPersistenceMapper {
    private UserPersistenceMapper() {}

    public static UserJpaEntity toJpaEntity(User user) {
        return new UserJpaEntity(
            user.getId(),
            user.getName(),
            user.getEmail(),
            user.getStatus(),
            user.getCreatedAt(),
            user.getUpdatedAt()
        );
    }

    public static User toDomainEntity(UserJpaEntity userJpaEntity) {
        return new User(
            userJpaEntity.getId(),
            userJpaEntity.getName(),
            userJpaEntity.getEmail(),
            userJpaEntity.getStatus(),
            userJpaEntity.getCreatedAt(),
            userJpaEntity.getUpdatedAt()
        );
    }

    public static List<User> toDomainEntityList(List<UserJpaEntity> userJpaEntities) {
        return userJpaEntities.stream()
            .map(UserPersistenceMapper::toDomainEntity)
            .toList();
    }
}
