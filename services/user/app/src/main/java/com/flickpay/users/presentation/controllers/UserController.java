package com.flickpay.users.presentation.controllers;

import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.flickpay.users.application.dto.CreateUserCommand;
import com.flickpay.users.application.service.CreateUserService;
import com.flickpay.users.application.service.GetUserService;
import com.flickpay.users.presentation.dto.CreateUserRequest;
import com.flickpay.users.presentation.dto.UserResponse;
import com.flickpay.users.presentation.mapper.UserResponseMapper;

@RestController
@RequestMapping("/users")
public class UserController {
    private final CreateUserService createUserService;
    private final GetUserService getUserService;

    public UserController(CreateUserService createUserService, GetUserService getUserService) {
        this.createUserService = createUserService;
        this.getUserService = getUserService;
    }

    @PostMapping
    public ResponseEntity<UserResponse> create(
        @Valid @RequestBody CreateUserRequest request
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

    @GetMapping
    public ResponseEntity<List<UserResponse>> findAll() {
        var users = getUserService.findAll();

        if (users.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok().body(UserResponseMapper.toResponseList(users));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable UUID id) {
        var user = getUserService.getById(id);

        return ResponseEntity.ok(UserResponseMapper.toResponse(user));
    }

    @GetMapping("/search")
    public ResponseEntity<List<UserResponse>> getUsersByEmail(
        @RequestParam(required = false) String email
    ) {
        var users = getUserService.findByEmail(email);

        return ResponseEntity.ok().body(UserResponseMapper.toResponseList(users));
    }
}
