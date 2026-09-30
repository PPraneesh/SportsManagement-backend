package com.praneesh.sports.team_service.client.dto;

public record InternalUserResponse(
        Long id,
        String name,
        String email,
        boolean active
) {
}