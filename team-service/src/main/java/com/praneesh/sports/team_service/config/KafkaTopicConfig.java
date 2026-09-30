package com.praneesh.sports.team_service.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaTopicConfig {
    public static final String TEAM_EVENTS = "team-events";

    public NewTopic teamEventsTopic(){
        return new NewTopic(
                TEAM_EVENTS,
                1,
                (short)1
        );
    }
}
