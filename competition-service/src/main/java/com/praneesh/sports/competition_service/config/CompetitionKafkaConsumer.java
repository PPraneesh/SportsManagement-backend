package com.praneesh.sports.competition_service.config;

import com.praneesh.sports.competition_service.dto.event.TournamentRegistrationClosedEvent;
import com.praneesh.sports.competition_service.enums.TournamentEventType;
import com.praneesh.sports.competition_service.service.CompetitionService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class CompetitionKafkaConsumer {

    private static final Logger logger =
            LoggerFactory.getLogger(
                    CompetitionKafkaConsumer.class
            );

    private final CompetitionService competitionService;

    public CompetitionKafkaConsumer(
            CompetitionService competitionService
    ) {

        this.competitionService =
                competitionService;
    }

    @KafkaListener(
            topics = KafkaTopicConfig.TOURNAMENT_EVENTS,
            groupId = "competition-service-group"
    )
    public void handleTournamentEvent(
            TournamentRegistrationClosedEvent event
    ) {

        logger.info(
                "Received tournament event: {} for tournament {}",
                event.eventType(),
                event.tournamentId()
        );

        if (event.eventType() !=
                TournamentEventType.TOURNAMENT_REGISTRATION_CLOSED) {

            return;
        }

        competitionService.generateFixtures(
                event.tournamentId()
        );

        logger.info(
                "Fixtures generated for tournament {}",
                event.tournamentId()
        );
    }
}