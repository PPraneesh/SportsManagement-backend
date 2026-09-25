package com.praneesh.sports.tournament_service.dto.response;

import java.time.LocalDateTime;

public record InternalTournamentRegistrationResponse(

        Long id,
        Long organizerId,
        String name,
        String visibility,
        String status,
        Integer maximumTeams,
        LocalDateTime registrationStart,
        LocalDateTime registrationEnd,

        Integer winPoints,
        Integer drawPoints,
        Integer lossPoints
) {
}