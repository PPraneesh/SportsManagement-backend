package com.praneesh.sports.tournament_service.util;


import com.praneesh.sports.tournament_service.exception.InvalidTournamentException;

import java.time.LocalDateTime;

public final class TournamentValidationUtil {

    private TournamentValidationUtil() {
    }

    public static void validateDates(
            LocalDateTime registrationStart,
            LocalDateTime registrationEnd,
            LocalDateTime startDate,
            LocalDateTime endDate
    ) {

        LocalDateTime now = LocalDateTime.now();

        if (registrationStart.isBefore(now)) {
            throw new InvalidTournamentException(
                    "Registration start cannot be in the past"
            );
        }

        if (!registrationEnd.isAfter(registrationStart)) {
            throw new InvalidTournamentException(
                    "Registration end must be after registration start"
            );
        }

        if (startDate.isBefore(registrationEnd)) {
            throw new InvalidTournamentException(
                    "Tournament start must be on or after registration end"
            );
        }

        if (endDate.isBefore(startDate)) {
            throw new InvalidTournamentException(
                    "Tournament end must be on or after tournament start"
            );
        }
    }

    public static void validatePoints(
            Integer winPoints,
            Integer drawPoints,
            Integer lossPoints
    ) {

        if (winPoints != null && winPoints < 1) {
            throw new InvalidTournamentException(
                    "Win points must be at least 1"
            );
        }

        if (drawPoints != null && drawPoints < 0) {
            throw new InvalidTournamentException(
                    "Draw points cannot be negative"
            );
        }

        if (lossPoints != null && lossPoints < 0) {
            throw new InvalidTournamentException(
                    "Loss points cannot be negative"
            );
        }
    }
}