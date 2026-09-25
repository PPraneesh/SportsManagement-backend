package com.praneesh.sports.team_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateTeamRequest(

        @NotBlank(message = "Team name is required")
        @Size(max = 150)
        String name,

        @NotBlank(message = "Short name is required")
        @Size(max = 30)
        String shortName,

        @Size(max = 500)
        String logoUrl,

        @Size(max = 1000)
        String description
) {
}