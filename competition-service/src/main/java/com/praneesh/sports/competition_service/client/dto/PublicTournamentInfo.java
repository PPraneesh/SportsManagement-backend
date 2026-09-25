package com.praneesh.sports.competition_service.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PublicTournamentInfo(

        Long id,
        String name,
        String sportType,
        String publicSlug,

        String visibility,
        String status,
        String format,

        Integer maximumTeams,

        Integer winPoints,
        Integer drawPoints,
        Integer lossPoints
) {
}