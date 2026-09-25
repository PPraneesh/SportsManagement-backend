package com.praneesh.sports.team_service.dto.response;

import java.time.LocalDateTime;

public record InternalTeamResponse(

        Long id,

        Long tournamentId,

        String name,

        String shortName,

        String status,

        LocalDateTime createdAt,

        Integer registrationOrder

) {
}