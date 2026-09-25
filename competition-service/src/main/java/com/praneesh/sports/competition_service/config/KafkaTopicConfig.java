package com.praneesh.sports.competition_service.config;

import org.apache.kafka.clients.admin.NewTopic;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaTopicConfig {

    public static final String TOURNAMENT_EVENTS =
            "tournament-events";

    public static final String COMPETITION_EVENTS =
            "competition-events";

    @Bean
    public NewTopic tournamentEventsTopic() {

        return new NewTopic(
                TOURNAMENT_EVENTS,
                1,
                (short) 1
        );
    }

    @Bean
    public NewTopic competitionEventsTopic() {

        return new NewTopic(
                COMPETITION_EVENTS,
                1,
                (short) 1
        );
    }
}