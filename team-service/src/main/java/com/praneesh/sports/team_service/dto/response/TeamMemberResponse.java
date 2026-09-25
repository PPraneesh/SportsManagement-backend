package com.praneesh.sports.team_service.dto.response;

import com.praneesh.sports.team_service.enums.TeamMemberRole;

import java.time.LocalDateTime;

public record TeamMemberResponse(
        Long id,
        Long teamId,
        Long userId,
        TeamMemberRole memberRole,
        boolean active,
        LocalDateTime joinedAt
) {
}