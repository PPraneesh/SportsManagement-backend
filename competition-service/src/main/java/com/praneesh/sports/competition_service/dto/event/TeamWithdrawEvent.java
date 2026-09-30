package com.praneesh.sports.competition_service.dto.event;


import com.praneesh.sports.competition_service.enums.TeamEventType;
import com.praneesh.sports.competition_service.enums.TournamentEventType;

import java.time.LocalDateTime;
import java.util.UUID;

public record TeamWithdrawEvent(

        TeamEventType eventType,

        UUID eventId,

        Long tournamentId,
        Long teamId,
        LocalDateTime occurredAt

) {
}