package com.praneesh.sports.tournament_service.util;
import com.praneesh.sports.tournament_service.entity.Tournament;
import com.praneesh.sports.tournament_service.enums.TournamentStatus;
import com.praneesh.sports.tournament_service.exception.TournamentOperationException;

import java.time.LocalDateTime;

import com.praneesh.sports.tournament_service.exception.InvalidTournamentException;


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
    public static void validateUpdateDates(
        Tournament tournament,
        LocalDateTime registrationStart,
        LocalDateTime registrationEnd,
        LocalDateTime startDate,
        LocalDateTime endDate
) {

    LocalDateTime now =
            LocalDateTime.now();

    if (tournament.getStatus() == TournamentStatus.DRAFT) {

        if (!registrationStart.isAfter(now)) {

            throw new TournamentOperationException(
                    "Registration start must be in the future"
            );
        }

        if (!registrationEnd.isAfter(registrationStart)) {

            throw new TournamentOperationException(
                    "Registration end must be after registration start"
            );
        }

        if (!startDate.isAfter(registrationEnd)) {

            throw new TournamentOperationException(
                    "Tournament start date must be after registration end"
            );
        }

        if (!endDate.isAfter(startDate)) {

            throw new TournamentOperationException(
                    "Tournament end date must be after tournament start date"
            );
        }

        return;
    }

    if (tournament.getStatus() == TournamentStatus.OPEN) {

        if (!registrationStart.isEqual(
                tournament.getRegistrationStart()
        )) {

            throw new TournamentOperationException(
                    "Registration start cannot be changed after registration opens"
            );
        }

        if (!registrationEnd.isAfter(now)) {

            throw new TournamentOperationException(
                    "Registration end must be in the future"
            );
        }

        if (!registrationEnd.isAfter(
                tournament.getRegistrationEnd()
        )) {

            throw new TournamentOperationException(
                    "Registration end can only be extended after registration opens"
            );
        }

        if (!registrationEnd.isBefore(
                tournament.getStartDate()
        )) {

            throw new TournamentOperationException(
                    "Registration must end before the tournament starts"
            );
        }

        if (!startDate.isEqual(
                tournament.getStartDate()
        )) {

            throw new TournamentOperationException(
                    "Tournament start date cannot be changed after registration opens"
            );
        }


        if (!endDate.isEqual(
                tournament.getEndDate()
        )) {

            throw new TournamentOperationException(
                    "Tournament end date cannot be changed after registration opens"
            );
        }

        return;
    }
}
}