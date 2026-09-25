package com.praneesh.sports.competition_service.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record CompleteMatchRequest(

        @NotNull
        @PositiveOrZero
        Integer teamAScore,

        @NotNull
        @PositiveOrZero
        Integer teamBScore,

        @NotNull
        @DecimalMin("0.0")
        Double teamARunRate,

        @NotNull
        @DecimalMin("0.0")
        Double teamBRunRate,

        String tieBreakerDescription,

        Long winnerTeamId
) {
}