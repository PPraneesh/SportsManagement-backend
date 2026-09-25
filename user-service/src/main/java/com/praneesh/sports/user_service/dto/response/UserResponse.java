package com.praneesh.sports.user_service.dto.response;

import java.time.LocalDateTime;

public record UserResponse(
        Long id,
        String name,
        String email,
        boolean active,
        LocalDateTime createdAt
) {
}
