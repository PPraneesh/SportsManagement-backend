package com.praneesh.sports.user_service.controller;


import com.praneesh.sports.user_service.dto.request.LoginRequest;
import com.praneesh.sports.user_service.dto.request.RegisterUserRequest;
import com.praneesh.sports.user_service.dto.response.LoginResponse;
import com.praneesh.sports.user_service.dto.response.UserResponse;
import com.praneesh.sports.user_service.service.UserService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> registerUser(
            @Valid @RequestBody RegisterUserRequest request
    ) {
        UserResponse response = userService.registerUser(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> loginUser(
            @Valid @RequestBody LoginRequest request
    ) {

        LoginResponse response =
                userService.loginUser(request);

        return ResponseEntity.ok(response);
    }
}