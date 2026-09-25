package com.praneesh.sports.team_service.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

public record CreateInvitationRequest(

        @NotBlank(message = "Captain email is required")
        @Email(message = "Enter a valid captain email")
        String invitedCaptainEmail,

        LocalDateTime expiresAt
) {
}