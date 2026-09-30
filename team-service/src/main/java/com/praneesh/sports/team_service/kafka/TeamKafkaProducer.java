package com.praneesh.sports.team_service.kafka;

import com.praneesh.sports.team_service.config.KafkaTopicConfig;
import com.praneesh.sports.team_service.dto.request.TeamWithdrawEvent;
import com.praneesh.sports.team_service.enums.TeamEventType;
import lombok.AllArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
@AllArgsConstructor
public class TeamKafkaProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishTeamWithdraw(
        Long tournamentId,
        Long teamId
    ){
        TeamWithdrawEvent event = new TeamWithdrawEvent(
                TeamEventType.TEAM_WITHDRAWN,
                UUID.randomUUID(),
                tournamentId,
                teamId,
                LocalDateTime.now()
        );

        kafkaTemplate.send(
                KafkaTopicConfig.TEAM_EVENTS,
                event
        );
    }
}
