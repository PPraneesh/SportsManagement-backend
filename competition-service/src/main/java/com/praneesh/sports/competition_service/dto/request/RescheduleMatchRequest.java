package com.praneesh.sports.competition_service.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record RescheduleMatchRequest(

        @NotNull
        @Future
        LocalDateTime scheduledAt
) {
}