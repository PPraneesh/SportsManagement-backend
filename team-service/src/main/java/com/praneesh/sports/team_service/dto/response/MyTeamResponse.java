package com.praneesh.sports.team_service.dto.response;

import com.praneesh.sports.team_service.client.dto.TournamentRegistrationInfo;
import com.praneesh.sports.team_service.enums.TeamMemberRole;
import com.praneesh.sports.team_service.enums.TeamStatus;

public record MyTeamResponse(
        Long teamId,
        Long tournamentId,
        String teamName,
        String shortName,
        String logoUrl,
        TeamStatus teamStatus,
        TeamMemberRole memberRole,
        TournamentRegistrationInfo tournament
) {
}