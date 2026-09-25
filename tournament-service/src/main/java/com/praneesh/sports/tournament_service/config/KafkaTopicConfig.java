package com.praneesh.sports.tournament_service.config;

import org.apache.kafka.clients.admin.NewTopic;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaTopicConfig {

    public static final String TOURNAMENT_EVENTS =
            "tournament-events";

    @Bean
    public NewTopic tournamentEventsTopic() {

        return new NewTopic(
                TOURNAMENT_EVENTS,
                1,
                (short) 1
        );
    }
}