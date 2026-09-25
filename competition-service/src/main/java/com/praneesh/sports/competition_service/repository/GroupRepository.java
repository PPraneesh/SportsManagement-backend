package com.praneesh.sports.competition_service.repository;

import com.praneesh.sports.competition_service.entity.Group;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GroupRepository
        extends JpaRepository<Group, Long> {

    boolean existsByTournamentId(
            Long tournamentId
    );

    List<Group> findAllByTournamentIdOrderBySequenceNumberAsc(
            Long tournamentId
    );

    Optional<Group> findByTournamentIdAndSequenceNumber(
            Long tournamentId,
            Integer sequenceNumber
    );
}