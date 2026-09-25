package com.praneesh.sports.competition_service.repository;

import com.praneesh.sports.competition_service.entity.Match;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MatchRepository
        extends JpaRepository<Match, Long> {

    boolean existsByTournamentId(
            Long tournamentId
    );

    List<Match>
    findAllByTournamentIdOrderByRoundNumberAscMatchNumberAsc(
            Long tournamentId
    );

    List<Match>
    findAllByGroupIdOrderByMatchNumberAsc(
            Long groupId
    );

    Optional<Match>
    findByIdAndTournamentId(
            Long matchId,
            Long tournamentId
    );

    Optional<Match>
    findByTournamentIdAndMatchCode(
            Long tournamentId,
            String matchCode
    );
}