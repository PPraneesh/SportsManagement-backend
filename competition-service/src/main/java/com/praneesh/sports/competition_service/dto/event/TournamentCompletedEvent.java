package com.praneesh.sports.competition_service.dto.event;

import com.praneesh.sports.competition_service.enums.CompetitionEventType;

import java.time.LocalDateTime;
import java.util.UUID;

public record TournamentCompletedEvent(

        CompetitionEventType eventType,

        UUID eventId,

        Long tournamentId,

        LocalDateTime occurredAt

) {
}