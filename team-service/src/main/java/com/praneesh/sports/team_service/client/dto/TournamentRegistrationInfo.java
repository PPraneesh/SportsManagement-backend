package com.praneesh.sports.team_service.client.dto;

import java.time.LocalDateTime;

public record TournamentRegistrationInfo(
        Long id,
        Long organizerId,
        String name,
        String visibility,
        String status,
        Integer maximumTeams,
        LocalDateTime registrationStart,
        LocalDateTime registrationEnd
) {
}