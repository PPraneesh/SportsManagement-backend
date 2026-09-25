package com.praneesh.sports.team_service.dto.response;

import com.praneesh.sports.team_service.enums.TeamStatus;

import java.time.LocalDateTime;

public record TeamResponse(
        Long id,
        Long tournamentId,
        Long captainId,
        String name,
        String shortName,
        String logoUrl,
        String description,
        TeamStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}