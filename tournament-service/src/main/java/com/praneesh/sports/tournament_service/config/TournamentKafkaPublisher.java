package com.praneesh.sports.tournament_service.config;

import com.praneesh.sports.tournament_service.dto.event.TournamentRegistrationClosedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class TournamentKafkaPublisher {

    private static final Logger logger =
            LoggerFactory.getLogger(TournamentKafkaPublisher.class);

    private final KafkaTemplate<String, TournamentRegistrationClosedEvent>
            kafkaTemplate;

    public TournamentKafkaPublisher(
            KafkaTemplate<String, TournamentRegistrationClosedEvent> kafkaTemplate
    ) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @TransactionalEventListener
    public void publishRegistrationClosedEvent(
            TournamentRegistrationClosedEvent event
    ) {

        logger.info(
                "Publishing tournament event: {} for tournament {}",
                event.eventType(),
                event.tournamentId()
        );

        kafkaTemplate.send(
                KafkaTopicConfig.TOURNAMENT_EVENTS,
                event.tournamentId().toString(),
                event
        ).whenComplete((result, exception) -> {

            if (exception != null) {
                logger.error(
                        "Failed to publish event for tournament {}",
                        event.tournamentId(),
                        exception
                );
                return;
            }

            logger.info(
                    "Tournament event published successfully: {}",
                    event.eventType()
            );
        });
    }
}