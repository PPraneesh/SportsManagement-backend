package com.praneesh.sports.competition_service.client.dto;

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