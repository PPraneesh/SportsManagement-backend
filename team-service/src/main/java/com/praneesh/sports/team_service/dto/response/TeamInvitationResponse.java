package com.praneesh.sports.team_service.dto.response;

import com.praneesh.sports.team_service.enums.InvitationStatus;

import java.time.LocalDateTime;

public record TeamInvitationResponse(
        Long id,
        Long tournamentId,
        Long invitedCaptainId,
        InvitationStatus status,
        String invitationUrl,
        LocalDateTime expiresAt,
        LocalDateTime respondedAt,
        LocalDateTime createdAt
) {
}