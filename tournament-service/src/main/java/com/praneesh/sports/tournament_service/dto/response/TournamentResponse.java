package com.praneesh.sports.tournament_service.dto.response;


import com.praneesh.sports.tournament_service.enums.TournamentFormat;
import com.praneesh.sports.tournament_service.enums.TournamentStatus;
import com.praneesh.sports.tournament_service.enums.TournamentVisibility;

import java.time.LocalDateTime;

public record TournamentResponse(

        Long id,
        Long organizerId,
        String name,
        String description,
        String sportType,
        String location,
        String publicSlug,
        TournamentVisibility visibility,
        TournamentStatus status,
        TournamentFormat format,
        Integer maximumTeams,
        Integer winPoints,
        Integer drawPoints,
        Integer lossPoints,
        LocalDateTime registrationStart,
        LocalDateTime registrationEnd,
        LocalDateTime startDate,
        LocalDateTime endDate,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}