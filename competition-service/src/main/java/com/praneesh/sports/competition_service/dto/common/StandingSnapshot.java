package com.praneesh.sports.competition_service.dto.common;

public record StandingSnapshot(

        Long groupId,
        Long teamId,

        int matchesPlayed,
        int wins,
        int draws,
        int losses,

        int points,

        int scoreFor,
        int scoreAgainst,
        int scoreDifference,

        double pointsPerMatch,
        double scoreDifferencePerMatch,
        double normalizedRunRate,
        double winPercentage
) {
}