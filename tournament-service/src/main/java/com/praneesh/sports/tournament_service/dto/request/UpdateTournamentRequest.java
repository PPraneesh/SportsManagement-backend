package com.praneesh.sports.tournament_service.dto.request;



import com.praneesh.sports.tournament_service.enums.TournamentVisibility;

import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

public record UpdateTournamentRequest(

        @NotBlank(message = "Tournament name is required")
        @Size(max = 150)
        String name,

        @Size(max = 2000)
        String description,

        @NotBlank(message = "Sport type is required")
        @Size(max = 100)
        String sportType,

        @NotBlank(message = "Location is required")
        @Size(max = 255)
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