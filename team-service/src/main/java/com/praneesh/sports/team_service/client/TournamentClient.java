package com.praneesh.sports.team_service.client;

import com.praneesh.sports.team_service.client.dto.TournamentRegistrationInfo;
import com.praneesh.sports.team_service.exception.InternalServiceException;
import com.praneesh.sports.team_service.exception.TournamentNotFoundException;

import org.springframework.beans.factory.annotation.Qualifier;

import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

public class TournamentClient {

    private final RestClient restClient;

    public TournamentClient(
            @Qualifier("tournamentRestClient")
            RestClient restClient
    ) {

        this.restClient = restClient;
    }

    public TournamentRegistrationInfo
    getRegistrationInfo(
            Long tournamentId
    ) {

        try {

            return restClient
                    .get()
                    .uri(
                            "/internal/tournaments/{id}/registration-info",
                            tournamentId
                    )
                    .retrieve()
                    .body(
                            TournamentRegistrationInfo.class
                    );

        } catch (HttpClientErrorException.NotFound e) {

            throw new TournamentNotFoundException(
                    "Tournament not found with id: "
                            + tournamentId
            );

        } catch (RestClientException e) {

            throw new InternalServiceException(
                    "Unable to communicate with Tournament Service"
            );
        }
    }

    public void closeRegistrationIfCapacityReached(
            Long tournamentId,
            long activeTeamCount
    ) {

        try {

            restClient
                    .post()
                    .uri(
                            uriBuilder ->
                                    uriBuilder
                                            .path(
                                                    "/internal/tournaments/{id}/close-if-capacity-reached"
                                            )
                                            .queryParam(
                                                    "activeTeamCount",
                                                    activeTeamCount
                                            )
                                            .build(tournamentId)
                    )
                    .retrieve()
                    .toBodilessEntity();

        } catch (RestClientException e) {

            /*
             * Do not fail team registration if this
             * best-effort status update fails.
             */
        }
    }
}
