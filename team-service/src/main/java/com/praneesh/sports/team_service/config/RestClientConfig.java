package com.praneesh.sports.team_service.config;

import com.praneesh.sports.team_service.client.TournamentClient;
import com.praneesh.sports.team_service.client.UserClient;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Bean("tournamentRestClient")
    public RestClient tournamentRestClient(
            @Value("${services.tournament.url}")
            String baseUrl
    ) {

        return RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    @Bean("userRestClient")
    public RestClient userRestClient(
            @Value("${services.user.url}")
            String baseUrl
    ) {

        return RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    @Bean
    public TournamentClient tournamentClient(
            @Qualifier("tournamentRestClient")
            RestClient restClient
    ) {

        return new TournamentClient(restClient);
    }

    @Bean
    public UserClient userClient(
            @Qualifier("userRestClient")
            RestClient restClient
    ) {

        return new UserClient(restClient);
    }
}