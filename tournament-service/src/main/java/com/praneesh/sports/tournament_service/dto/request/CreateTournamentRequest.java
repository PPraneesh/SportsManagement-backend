package com.praneesh.sports.tournament_service.dto.request;
import com.praneesh.sports.tournament_service.enums.TournamentVisibility;

import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

public record CreateTournamentRequest(

        @NotBlank(message = "Tournament name is required")
        @Size(max = 150, message = "Tournament name must not exceed 150 characters")
        String name,

        @Size(max = 2000, message = "Description must not exceed 2000 characters")
        String description,

        @NotBlank(message = "Sport type is required")
        @Size(max = 100, message = "Sport type must not exceed 100 characters")
        String sportType,

        @NotBlank(message = "Location is required")
        @Size(max = 255, message = "Location must not exceed 255 characters")
        String location,

        @NotNull(message = "Visibility is required")
        TournamentVisibility visibility,

        @NotNull(message = "Maximum teams is required")
        @Min(value = 4, message = "Tournament must allow at least 4 teams")
        Integer maximumTeams,

        @Min(value = 1, message = "Win points must be at least 1")
        Integer winPoints,

        @Min(value = 0, message = "Draw points cannot be negative")
        Integer drawPoints,

        @Min(value = 0, message = "Loss points cannot be negative")
        Integer lossPoints,

        @NotNull(message = "Registration start is required")
        LocalDateTime registrationStart,

        @NotNull(message = "Registration end is required")
        LocalDateTime registrationEnd,

        @NotNull(message = "Tournament start date is required")
        LocalDateTime startDate,

        @NotNull(message = "Tournament end date is required")
        LocalDateTime endDate
) {
}