package com.praneesh.sports.competition_service.dto.response;

import java.time.LocalDateTime;

public record PublicMatchResponse(

        Long matchId,
        Long tournamentId,

        String matchCode,
        String matchType,
        String status,

        Long groupId,
        String groupName,

        Long teamAId,
        String teamAName,

        Long teamBId,
        String teamBName,

        Long winnerTeamId,
        String winnerTeamName,

        Integer teamAScore,
        Integer teamBScore,

        Double teamARunRate,
        Double teamBRunRate,

        String tieBreakerDescription,

        LocalDateTime scheduledAt,
        LocalDateTime originalScheduledAt,
        LocalDateTime startedAt,
        LocalDateTime completedAt
) {
}