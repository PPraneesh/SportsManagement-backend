package com.praneesh.sports.team_service.repository;

import com.praneesh.sports.team_service.entity.TeamInvitation;
import com.praneesh.sports.team_service.enums.InvitationStatus;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TeamInvitationRepository
        extends JpaRepository<TeamInvitation, Long> {

    Optional<TeamInvitation> findByInvitationToken(
            String invitationToken
    );

    Optional<TeamInvitation>
    findByTournamentIdAndInvitedCaptainIdAndStatus(
            Long tournamentId,
            Long invitedCaptainId,
            InvitationStatus status
    );

    boolean existsByTournamentIdAndInvitedCaptainIdAndStatus(
            Long tournamentId,
            Long invitedCaptainId,
            InvitationStatus status
    );

    List<TeamInvitation> findAllByTournamentIdOrderByCreatedAtDesc(
            Long tournamentId
    );

    List<TeamInvitation>
    findAllByInvitedCaptainIdOrderByCreatedAtDesc(
            Long invitedCaptainId
    );
}