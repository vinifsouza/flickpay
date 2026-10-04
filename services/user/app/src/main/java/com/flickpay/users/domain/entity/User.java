package com.flickpay.users.domain.entity;

import java.time.OffsetDateTime;
import java.util.UUID;

import com.flickpay.users.domain.enums.UserStatus;
import com.flickpay.users.domain.exception.UserAlreadyInactiveException;

public class User {
    private UUID id;
    private String name;
    private String email;
    private UserStatus status;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public User(UUID id, String name, String email, UserStatus status, OffsetDateTime createdAt, OffsetDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public static User create(String name, String email) {
        return new User(
            UUID.randomUUID(),
            name,
            email,
            UserStatus.ACTIVE,
            OffsetDateTime.now(),
            OffsetDateTime.now()
        );
    }

    public void deactivate() {
        if (status == UserStatus.INACTIVE) {
            throw new UserAlreadyInactiveException(this.id.toString());
        }

        this.status = UserStatus.INACTIVE;
        this.updatedAt = OffsetDateTime.now();
    }
}
