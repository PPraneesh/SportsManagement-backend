package com.praneesh.sports.team_service.dto.request;

import jakarta.validation.constraints.NotNull;

public record AddTeamMemberRequest(

        @NotNull(message = "Email is required")
        String email
) {
}