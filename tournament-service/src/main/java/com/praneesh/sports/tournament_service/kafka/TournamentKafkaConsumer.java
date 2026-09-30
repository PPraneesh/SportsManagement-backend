package com.praneesh.sports.tournament_service.kafka;

import com.praneesh.sports.tournament_service.config.KafkaTopicConfig;
import com.praneesh.sports.tournament_service.dto.event.TournamentCompletedEvent;
import com.praneesh.sports.tournament_service.enums.CompetitionEventType;
import com.praneesh.sports.tournament_service.service.TournamentService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class TournamentKafkaConsumer {

    private static final Logger logger =
            LoggerFactory.getLogger(
                    TournamentKafkaConsumer.class
            );

    private final TournamentService tournamentService;

    public TournamentKafkaConsumer(
            TournamentService tournamentService
    ) {
        this.tournamentService =
                tournamentService;
    }

    @KafkaListener(
            topics = KafkaTopicConfig.COMPETITION_EVENTS,
            groupId = "tournament-service-group"
    )
    public void handleCompetitionEvent(
            TournamentCompletedEvent event
    ) {

        logger.info(
                "Received competition event: {} for tournament {}",
                event.eventType(),
                event.tournamentId()
        );

        if (event.eventType() !=
                CompetitionEventType.TOURNAMENT_COMPLETED) {

            return;
        }

        tournamentService.completeTournament(
                event.tournamentId()
        );

        logger.info(
                "Tournament {} marked as COMPLETED",
                event.tournamentId()
        );
    }
}