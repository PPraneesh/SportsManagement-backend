package com.praneesh.sports.team_service.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ManualTeamRequest(

        @NotBlank(message = "Team name is required")
        @Size(max = 150, message = "Team name must not exceed 150 characters")
        String name,

        @NotBlank(message = "Short name is required")
        @Size(max = 30, message = "Short name must not exceed 30 characters")
        String shortName,

        @Size(max = 500, message = "Logo URL must not exceed 500 characters")
        String logoUrl,

        @Size(max = 1000, message = "Description must not exceed 1000 characters")
        String description,

        @NotBlank(message = "Captain email is required")
        @Email(message = "Enter a valid captain email")
        String captainEmail
) {
}