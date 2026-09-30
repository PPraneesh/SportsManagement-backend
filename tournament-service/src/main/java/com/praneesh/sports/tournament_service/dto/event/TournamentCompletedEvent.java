package com.praneesh.sports.tournament_service.dto.event;

import com.praneesh.sports.tournament_service.enums.CompetitionEventType;

import java.time.LocalDateTime;
import java.util.UUID;

public record TournamentCompletedEvent(

        CompetitionEventType eventType,

        UUID eventId,

        Long tournamentId,

        LocalDateTime occurredAt

) {
}