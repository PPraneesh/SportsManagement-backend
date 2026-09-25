package com.praneesh.sports.user_service.mapper;

import com.praneesh.sports.user_service.dto.request.RegisterUserRequest;
import com.praneesh.sports.user_service.dto.response.UserResponse;
import com.praneesh.sports.user_service.entity.User;


import java.util.Locale;

public final class UserMapper {

    private UserMapper() {
    }

    public static User toEntity(
            RegisterUserRequest request,
            String passwordHash
    ) {
        return new User(
                request.name().trim(),
                request.email().trim().toLowerCase(Locale.ROOT),
                passwordHash
        );
    }

    public static UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.isActive(),
                user.getCreatedAt()
        );
    }
}