package com.flickpay.users.presentation.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.flickpay.users.application.dto.CreateUserCommand;
import com.flickpay.users.application.service.CreateUserService;
import com.flickpay.users.presentation.dto.CreateUserRequest;
import com.flickpay.users.presentation.dto.UserResponse;
import com.flickpay.users.presentation.mapper.UserResponseMapper;

@RestController
@RequestMapping("/users")
public class UserController {
    private final CreateUserService createUserService;

    public UserController(CreateUserService createUserService) {
        this.createUserService = createUserService;
    }

    @PostMapping
    public ResponseEntity<UserResponse> create(
        @RequestBody CreateUserRequest request
    ) {
        var command = new CreateUserCommand(
            request.name(),
            request.email()
        );
        
        var user = createUserService.create(command);

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(UserResponseMapper.toResponse(user));
    }
}
