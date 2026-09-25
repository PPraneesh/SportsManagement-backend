package com.praneesh.sports.competition_service.dto.response;

public record GroupResponse(

        Long id,
        Long tournamentId,
        String name,
        Integer sequenceNumber,
        String status
) {
}