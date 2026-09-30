package com.praneesh.sports.competition_service.kafka;

import com.praneesh.sports.competition_service.config.KafkaTopicConfig;
import com.praneesh.sports.competition_service.dto.event.TournamentCompletedEvent;
import com.praneesh.sports.competition_service.enums.CompetitionEventType;

import lombok.AllArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
@AllArgsConstructor
public class CompetitionKafkaProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishTournamentCompleted(
            Long tournamentId
    ) {

        TournamentCompletedEvent event =
                new TournamentCompletedEvent(
                        CompetitionEventType.TOURNAMENT_COMPLETED,
                        UUID.randomUUID(),
                        tournamentId,
                        LocalDateTime.now()
                );

        kafkaTemplate.send(
                KafkaTopicConfig.COMPETITION_EVENTS,
                String.valueOf(tournamentId),
                event
        );
    }
}