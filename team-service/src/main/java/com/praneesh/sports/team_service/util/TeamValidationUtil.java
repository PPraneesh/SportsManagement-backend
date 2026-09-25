package com.praneesh.sports.team_service.util;

import com.praneesh.sports.team_service.client.dto.TournamentRegistrationInfo;
import com.praneesh.sports.team_service.exception.TeamOperationException;

import java.time.LocalDateTime;

public final class TeamValidationUtil {

    private TeamValidationUtil() {
    }

    public static void validateRegistration(
            TournamentRegistrationInfo tournament,
            long activeTeamCount
    ) {

        if (!"OPEN".equals(tournament.status())) {
            throw new TeamOperationException(
                    "Team registration is currently closed"
            );
        }

        LocalDateTime now = LocalDateTime.now();

        if (now.isBefore(
                tournament.registrationStart()
        )) {

            throw new TeamOperationException(
                    "Team registration has not started"
            );
        }

        if (now.isAfter(
                tournament.registrationEnd()
        )) {

            throw new TeamOperationException(
                    "Team registration has ended"
            );
        }

        if (activeTeamCount >=
                tournament.maximumTeams()) {

            throw new TeamOperationException(
                    "Maximum team capacity has been reached"
            );
        }
    }

    public static void validateTeamModifiable(
            TournamentRegistrationInfo tournament
    ) {

        String status = tournament.status();

        if ("FIXTURES_GENERATED".equals(status) ||
                "IN_PROGRESS".equals(status) ||
                "COMPLETED".equals(status) ||
                "CANCELLED".equals(status)) {

            throw new TeamOperationException(
                    "Team can no longer be modified"
            );
        }
    }
}