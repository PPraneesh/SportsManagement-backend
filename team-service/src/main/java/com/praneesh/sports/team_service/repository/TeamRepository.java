package com.praneesh.sports.team_service.repository;

import com.praneesh.sports.team_service.entity.Team;
import com.praneesh.sports.team_service.enums.TeamStatus;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TeamRepository
        extends JpaRepository<Team, Long> {

    List<Team> findAllByTournamentIdOrderByCreatedAtAsc(
            Long tournamentId
    );

    List<Team> findAllByTournamentIdAndStatusOrderByCreatedAtAsc(
            Long tournamentId,
            TeamStatus status
    );

    Optional<Team> findByIdAndTournamentId(
            Long teamId,
            Long tournamentId
    );

    Optional<Team> findByTournamentIdAndCaptainId(
            Long tournamentId,
            Long captainId
    );

    boolean existsByTournamentIdAndCaptainId(
            Long tournamentId,
            Long captainId
    );

    long countByTournamentIdAndStatus(
            Long tournamentId,
            TeamStatus status
    );
}