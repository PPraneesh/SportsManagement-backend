package com.praneesh.sports.competition_service.client;

import com.praneesh.sports.competition_service.client.dto.InternalTeamResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
public class TeamClient {

    private final RestClient restClient;

    public TeamClient(
            @Value("${services.team.url}") String teamServiceUrl
    ) {

        this.restClient =
                RestClient.builder()
                        .baseUrl(teamServiceUrl)
                        .build();
    }

    public List<InternalTeamResponse>
    getTournamentTeams(
            Long tournamentId
    ) {

        return restClient
                .get()
                .uri(
                        "/internal/tournaments/{tournamentId}/teams",
                        tournamentId
                )
                .retrieve()
                .body(
                        new ParameterizedTypeReference<
                                List<InternalTeamResponse>
                                >() {
                        }
                );
    }
}