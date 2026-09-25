package com.praneesh.sports.competition_service.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TournamentRegistrationInfo(

        Long id,
        Long organizerId,
        String name,
        String visibility,
        String status,
        Integer maximumTeams,
        LocalDateTime registrationStart,
        LocalDateTime registrationEnd,

        Integer winPoints,
        Integer drawPoints,
        Integer lossPoints
) {
}