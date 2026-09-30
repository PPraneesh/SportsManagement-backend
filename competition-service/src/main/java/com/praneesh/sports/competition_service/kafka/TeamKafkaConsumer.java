package com.praneesh.sports.competition_service.kafka;

import com.praneesh.sports.competition_service.config.KafkaTopicConfig;
import com.praneesh.sports.competition_service.dto.event.TeamWithdrawEvent;
import com.praneesh.sports.competition_service.enums.TeamEventType;
import com.praneesh.sports.competition_service.service.CompetitionService;

import lombok.AllArgsConstructor;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class TeamKafkaConsumer {

    private final CompetitionService competitionService;

    @KafkaListener(
            topics = KafkaTopicConfig.TEAM_EVENTS,
            groupId = "competition-service-team-group",
            containerFactory = "teamKafkaListenerContainerFactory"
    )
    public void handleTeamEvent(
            TeamWithdrawEvent event
    ) {

        if (event.eventType() !=
                TeamEventType.TEAM_WITHDRAWN) {

            return;
        }

        competitionService.handleTeamWithdrawal(
                event.tournamentId(),
                event.teamId()
        );
    }
}