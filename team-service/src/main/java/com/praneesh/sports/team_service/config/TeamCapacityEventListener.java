package com.praneesh.sports.team_service.config;

import com.praneesh.sports.team_service.client.TournamentClient;
import com.praneesh.sports.team_service.dto.event.TeamCapacityReachedEvent;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class TeamCapacityEventListener {

    private final TournamentClient tournamentClient;

    public TeamCapacityEventListener(
            TournamentClient tournamentClient
    ) {

        this.tournamentClient =
                tournamentClient;
    }

    @TransactionalEventListener
    public void handleCapacityReached(
            TeamCapacityReachedEvent event
    ) {

        tournamentClient.closeRegistrationIfCapacityReached(
                event.tournamentId(),
                event.activeTeamCount()
        );
    }
}