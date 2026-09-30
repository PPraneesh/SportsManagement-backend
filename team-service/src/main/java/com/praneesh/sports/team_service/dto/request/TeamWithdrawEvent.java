package com.praneesh.sports.team_service.dto.request;


import com.praneesh.sports.team_service.enums.TeamEventType;

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