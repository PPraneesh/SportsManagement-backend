package com.praneesh.sports.competition_service.client;

import com.praneesh.sports.competition_service.client.dto.PublicTournamentInfo;
import com.praneesh.sports.competition_service.client.dto.TournamentRegistrationInfo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class TournamentClient {

    private final RestClient restClient;

    public TournamentClient(
            @Value("${services.tournament.url}")
            String tournamentServiceUrl
    ) {

        this.restClient =
                RestClient.builder()
                        .baseUrl(tournamentServiceUrl)
                        .build();
    }

    public TournamentRegistrationInfo
    getRegistrationInfo(
            Long tournamentId
    ) {

        return restClient
                .get()
                .uri(
                        "/internal/tournaments/{tournamentId}/registration-info",
                        tournamentId
                )
                .retrieve()
                .body(
                        TournamentRegistrationInfo.class
                );
    }

    public PublicTournamentInfo
    getPublicTournament(
            String slug
    ) {

        return restClient
                .get()
                .uri(
                        "/api/tournaments/public/{slug}",
                        slug
                )
                .retrieve()
                .body(
                        PublicTournamentInfo.class
                );
    }
}