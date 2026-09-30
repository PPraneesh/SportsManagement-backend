package com.praneesh.sports.competition_service.config;

import com.praneesh.sports.competition_service.dto.event.TeamWithdrawEvent;
import com.praneesh.sports.competition_service.dto.event.TournamentRegistrationClosedEvent;

import org.apache.kafka.common.serialization.StringDeserializer;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;

import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConsumerConfig {

    private Map<String, Object> baseConsumerProperties() {

        Map<String, Object> props = new HashMap<>();

        props.put(
                org.apache.kafka.clients.consumer.ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                "localhost:9092"
        );

        props.put(
                org.apache.kafka.clients.consumer.ConsumerConfig.GROUP_ID_CONFIG,
                "competition-service-group"
        );

        props.put(
                org.apache.kafka.clients.consumer.ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,
                "earliest"
        );

        props.put(
                org.apache.kafka.clients.consumer.ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                ErrorHandlingDeserializer.class
        );

        props.put(
                org.apache.kafka.clients.consumer.ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                ErrorHandlingDeserializer.class
        );

        props.put(
                ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS,
                StringDeserializer.class
        );

        return props;
    }


    // ============================================================
    // TOURNAMENT EVENTS
    // ============================================================

    @Bean
    public ConsumerFactory<String, TournamentRegistrationClosedEvent>
    tournamentConsumerFactory() {

        Map<String, Object> props = baseConsumerProperties();

        JacksonJsonDeserializer<TournamentRegistrationClosedEvent>
                jsonDeserializer =
                new JacksonJsonDeserializer<>(
                        TournamentRegistrationClosedEvent.class
                );

        jsonDeserializer.addTrustedPackages(
                "com.praneesh.sports.competition_service.dto.event"
        );

        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                new ErrorHandlingDeserializer<>(jsonDeserializer)
        );
    }


    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, TournamentRegistrationClosedEvent>
    tournamentKafkaListenerContainerFactory() {

        ConcurrentKafkaListenerContainerFactory<String, TournamentRegistrationClosedEvent>
                factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(
                tournamentConsumerFactory()
        );

        return factory;
    }


    // ============================================================
    // TEAM EVENTS
    // ============================================================

    @Bean
    public ConsumerFactory<String, TeamWithdrawEvent>
    teamConsumerFactory() {

        Map<String, Object> props = baseConsumerProperties();

        JacksonJsonDeserializer<TeamWithdrawEvent>
                jsonDeserializer =
                new JacksonJsonDeserializer<>(
                        TeamWithdrawEvent.class
                );

        jsonDeserializer.addTrustedPackages(
                "com.praneesh.sports.competition_service.dto.event"
        );

        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                new ErrorHandlingDeserializer<>(jsonDeserializer)
        );
    }


    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, TeamWithdrawEvent>
    teamKafkaListenerContainerFactory() {

        ConcurrentKafkaListenerContainerFactory<String, TeamWithdrawEvent>
                factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(
                teamConsumerFactory()
        );

        return factory;
    }
}