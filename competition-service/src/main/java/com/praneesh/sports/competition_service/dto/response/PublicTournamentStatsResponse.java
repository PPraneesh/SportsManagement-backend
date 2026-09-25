package com.praneesh.sports.competition_service.dto.response;

import java.util.List;

public record PublicTournamentStatsResponse(

        Long tournamentId,
        String publicSlug,

        String name,
        String sportType,

        String visibility,
        String status,
        String format,

        Integer totalTeams,
        Integer totalMatches,
        Integer completedMatches,
        Integer liveMatches,
        Integer upcomingMatches,

        String currentStage,

        Long championTeamId,
        String championTeamName,

        List<PublicGroupResponse> groups
) {
}