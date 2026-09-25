package com.praneesh.sports.user_service.dto.response;

public record InternalUserResponse(
        Long id,
        String name,
        String email,
        boolean active
) {
}