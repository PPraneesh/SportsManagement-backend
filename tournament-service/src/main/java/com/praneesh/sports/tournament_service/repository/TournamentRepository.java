package com.praneesh.sports.tournament_service.repository;


import com.praneesh.sports.tournament_service.entity.Tournament;
import com.praneesh.sports.tournament_service.enums.TournamentStatus;
import com.praneesh.sports.tournament_service.enums.TournamentVisibility;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TournamentRepository
        extends JpaRepository<Tournament, Long> {

    Optional<Tournament> findByPublicSlug(String publicSlug);

    boolean existsByPublicSlug(String publicSlug);

    List<Tournament> findByOrganizerIdOrderByCreatedAtDesc(
            Long organizerId
    );

    @Query("""
            SELECT t
            FROM Tournament t
            WHERE t.visibility = :visibility
              AND t.status = :status
              AND t.registrationStart <= :now
              AND t.registrationEnd >= :now
            ORDER BY t.startDate ASC
            """)
    List<Tournament> findActivePublicTournaments(
            @Param("visibility") TournamentVisibility visibility,
            @Param("status") TournamentStatus status,
            @Param("now") LocalDateTime now
    );
}