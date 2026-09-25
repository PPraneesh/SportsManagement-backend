package com.praneesh.sports.competition_service.mapper;

import com.praneesh.sports.competition_service.dto.response.MatchResponse;
import com.praneesh.sports.competition_service.entity.Match;

public final class MatchMapper {

    private MatchMapper() {
    }

    public static MatchResponse toResponse(
            Match match
    ) {

        return new MatchResponse(
                match.getId(),
                match.getTournamentId(),
                match.getGroupId(),
                match.getTeamAId(),
                match.getTeamBId(),
                match.getWinnerTeamId(),
                match.getMatchCode(),
                match.getMatchType(),
                match.getStatus(),
                match.getRoundNumber(),
                match.getMatchNumber(),
                match.getTeamAScore(),
                match.getTeamBScore(),
                match.getTeamARunRate(),
                match.getTeamBRunRate(),
                match.getTieBreakerDescription(),
                match.getScheduledAt(),
                match.getOriginalScheduledAt(),
                match.getStartedAt(),
                match.getCompletedAt()
        );
    }
}