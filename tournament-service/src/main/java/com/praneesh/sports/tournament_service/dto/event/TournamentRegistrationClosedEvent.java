package com.praneesh.sports.tournament_service.dto.event;

import com.praneesh.sports.tournament_service.enums.TournamentEventType;

import java.time.LocalDateTime;
import java.util.UUID;

public record TournamentRegistrationClosedEvent(

        TournamentEventType eventType,

        UUID eventId,

        Long tournamentId,

        LocalDateTime occurredAt

) {
}