package com.praneesh.sports.user_service.service;

import com.praneesh.sports.user_service.dto.request.RegisterUserRequest;
import com.praneesh.sports.user_service.dto.response.UserResponse;


import com.praneesh.sports.user_service.dto.request.LoginRequest;
import com.praneesh.sports.user_service.dto.response.LoginResponse;

public interface UserService {

    UserResponse registerUser(RegisterUserRequest request);

    LoginResponse loginUser(LoginRequest request);
}