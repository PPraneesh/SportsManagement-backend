package com.praneesh.sports.competition_service.dto.response;

public record StandingResponse(

        Long groupId,
        Long teamId,
        String teamName,

        Integer rank,

        Integer matchesPlayed,
        Integer wins,
        Integer draws,
        Integer losses,

        Integer points,

        Integer scoreFor,
        Integer scoreAgainst,
        Integer scoreDifference,

        Double pointsPerMatch,
        Double scoreDifferencePerMatch,
        Double normalizedRunRate,
        Double winPercentage,

        Boolean qualified
) {
}