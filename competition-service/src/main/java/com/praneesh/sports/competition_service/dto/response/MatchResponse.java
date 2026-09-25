package com.praneesh.sports.competition_service.dto.response;

import com.praneesh.sports.competition_service.enums.MatchStatus;
import com.praneesh.sports.competition_service.enums.MatchType;

import java.time.LocalDateTime;

public record MatchResponse(

        Long id,

        Long tournamentId,

        Long groupId,

        Long teamAId,

        Long teamBId,

        Long winnerTeamId,

        String matchCode,

        MatchType matchType,

        MatchStatus status,

        Integer roundNumber,

        Integer matchNumber,

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