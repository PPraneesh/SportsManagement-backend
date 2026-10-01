package com.praneesh.sports.user_service.controller;

import com.praneesh.sports.user_service.dto.request.LoginRequest;
import com.praneesh.sports.user_service.dto.request.RegisterUserRequest;
import com.praneesh.sports.user_service.dto.response.LoginResponse;
import com.praneesh.sports.user_service.dto.response.UserResponse;
import com.praneesh.sports.user_service.service.UserService;

import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> registerUser(
            @Valid @RequestBody RegisterUserRequest request
    ) {
        log.info("Received request to register user with email: {}", request.email());
        UserResponse response = userService.registerUser(request);
        log.info("Successfully registered user with id: {}", response.id());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> loginUser(
            @Valid @RequestBody LoginRequest request
    ) {
        log.info("Received login request for email: {}", request.email());
        LoginResponse response =
                userService.loginUser(request);
        log.info("User logged in successfully: {}", request.email());

        return ResponseEntity.ok(response);
    }
}