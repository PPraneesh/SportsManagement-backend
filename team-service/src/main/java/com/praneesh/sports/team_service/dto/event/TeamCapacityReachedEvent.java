package com.praneesh.sports.team_service.dto.event;

public record TeamCapacityReachedEvent(

        Long tournamentId,

        long activeTeamCount

) {
}