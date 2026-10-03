package com.flickpay.users.presentation.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.flickpay.users.application.dto.CreateUserCommand;
import com.flickpay.users.application.service.CreateUserService;
import com.flickpay.users.presentation.dto.CreateUserRequest;
import com.flickpay.users.presentation.dto.UserResponse;
import com.flickpay.users.presentation.mapper.UserResponseMapper;


@RestController
@RequestMapping("/users")
public class UserController {
    private final UserResponseMapper userResponseMapper;
    private final CreateUserService createUserService;

    public UserController(UserResponseMapper userResponseMapper, CreateUserService createUserService) {
        this.userResponseMapper = userResponseMapper;
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
            .body(userResponseMapper.toResponse(user));
    }
}
