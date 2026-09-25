package com.praneesh.sports.team_service.service;

import com.praneesh.sports.team_service.client.TournamentClient;
import com.praneesh.sports.team_service.client.UserClient;
import com.praneesh.sports.team_service.client.dto.InternalUserResponse;
import com.praneesh.sports.team_service.client.dto.TournamentRegistrationInfo;

import com.praneesh.sports.team_service.dto.request.CreateInvitationRequest;
import com.praneesh.sports.team_service.dto.response.TeamInvitationResponse;

import com.praneesh.sports.team_service.entity.Team;
import com.praneesh.sports.team_service.entity.TeamInvitation;

import com.praneesh.sports.team_service.enums.InvitationStatus;
import com.praneesh.sports.team_service.enums.TeamStatus;

import com.praneesh.sports.team_service.exception.InvitationNotFoundException;
import com.praneesh.sports.team_service.exception.TeamAccessDeniedException;
import com.praneesh.sports.team_service.exception.TeamOperationException;

import com.praneesh.sports.team_service.mapper.TeamMapper;

import com.praneesh.sports.team_service.repository.TeamInvitationRepository;
import com.praneesh.sports.team_service.repository.TeamRepository;

import com.praneesh.sports.team_service.util.InvitationTokenUtil;
import com.praneesh.sports.team_service.util.InvitationUtil;
import com.praneesh.sports.team_service.util.SecurityUtils;
import com.praneesh.sports.team_service.util.TeamValidationUtil;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class InvitationServiceImpl
        implements InvitationService {

    private final TeamInvitationRepository invitationRepository;
    private final TeamRepository teamRepository;

    private final TournamentClient tournamentClient;
    private final UserClient userClient;

    private final String frontendBaseUrl;

    public InvitationServiceImpl(
            TeamInvitationRepository invitationRepository,
            TeamRepository teamRepository,
            TournamentClient tournamentClient,
            UserClient userClient,
            @Value("${app.frontend-base-url}")
            String frontendBaseUrl
    ) {

        this.invitationRepository =
                invitationRepository;

        this.teamRepository =
                teamRepository;

        this.tournamentClient =
                tournamentClient;

        this.userClient =
                userClient;

        this.frontendBaseUrl =
                frontendBaseUrl;
    }

    @Override
    public TeamInvitationResponse createInvitation(
            Long tournamentId,
            CreateInvitationRequest request
    ) {

        Long organizerId =
                SecurityUtils.requireCurrentUserId();

        TournamentRegistrationInfo tournament =
                tournamentClient.getRegistrationInfo(
                        tournamentId
                );

        if (!tournament.organizerId()
                .equals(organizerId)) {

            throw new TeamAccessDeniedException(
                    "Only the tournament organizer can send invitations"
            );
        }

        long activeTeamCount =
                teamRepository
                        .countByTournamentIdAndStatus(
                                tournamentId,
                                TeamStatus.ACTIVE
                        );

        TeamValidationUtil.validateRegistration(
                tournament,
                activeTeamCount
        );

        InternalUserResponse invitedCaptain = userClient.verifyActiveUserByEmail(
                request.invitedCaptainEmail()
        );
        Long invitedCaptainId = invitedCaptain.id();
        if (teamRepository
                .existsByTournamentIdAndCaptainId(
                        tournamentId,
                        invitedCaptainId
                )) {

            throw new TeamOperationException(
                    "This user already has a team in the tournament"
            );
        }

        if (invitationRepository
                .existsByTournamentIdAndInvitedCaptainIdAndStatus(
                        tournamentId,
                        invitedCaptainId,
                        InvitationStatus.PENDING
                )) {

            throw new TeamOperationException(
                    "A pending invitation already exists for this user"
            );
        }

        LocalDateTime expiresAt = request.expiresAt();

        if (expiresAt == null) {
            expiresAt = tournament.registrationEnd();
        }

        LocalDateTime now =
                LocalDateTime.now();

        if (!expiresAt.isAfter(now)) {

            throw new TeamOperationException(
                    "Invitation expiry must be in the future"
            );
        }

        if (expiresAt.isAfter(
                tournament.registrationEnd()
        )) {

            throw new TeamOperationException(
                    "Invitation cannot expire after registration ends"
            );
        }

        String token =
                InvitationTokenUtil.generateToken();

        TeamInvitation invitation =
                TeamMapper.toEntity(
                        request,
                        tournamentId,
                        invitedCaptainId,
                        token,
                        expiresAt
                );

        TeamInvitation saved =
                invitationRepository.save(invitation);

        String invitationUrl =
                InvitationUtil.buildInvitationUrl(
                        frontendBaseUrl,
                        token
                );

        return TeamMapper.toResponse(
                saved,
                invitationUrl
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<TeamInvitationResponse>
    getTournamentInvitations(
            Long tournamentId
    ) {

        Long organizerId =
                SecurityUtils.requireCurrentUserId();

        TournamentRegistrationInfo tournament =
                tournamentClient.getRegistrationInfo(
                        tournamentId
                );

        if (!tournament.organizerId()
                .equals(organizerId)) {

            throw new TeamAccessDeniedException(
                    "Only the tournament organizer can view invitations"
            );
        }

        return invitationRepository
                .findAllByTournamentIdOrderByCreatedAtDesc(
                        tournamentId
                )
                .stream()
                .map(this::prepareResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TeamInvitationResponse>
    getMyInvitations() {

        Long userId =
                SecurityUtils.requireCurrentUserId();

        return invitationRepository
                .findAllByInvitedCaptainIdOrderByCreatedAtDesc(
                        userId
                )
                .stream()
                .map(this::prepareResponse)
                .toList();
    }

    @Override
    public TeamInvitationResponse
    acceptInvitation(
            String token
    ) {

        Long currentUserId =
                SecurityUtils.requireCurrentUserId();

        TeamInvitation invitation =
                findInvitation(token);

        verifyInvitationOwner(
                invitation,
                currentUserId
        );

        refreshExpiration(invitation);

        if (invitation.getStatus() !=
                InvitationStatus.PENDING) {

            throw new TeamOperationException(
                    "Invitation is no longer pending"
            );
        }

        if (invitation.getStatus() ==
                InvitationStatus.EXPIRED) {

            throw new TeamOperationException(
                    "Invitation has expired"
            );
        }

        TournamentRegistrationInfo tournament =
                tournamentClient.getRegistrationInfo(
                        invitation.getTournamentId()
                );

        long activeTeamCount =
                teamRepository
                        .countByTournamentIdAndStatus(
                                invitation.getTournamentId(),
                                TeamStatus.ACTIVE
                        );

        TeamValidationUtil.validateRegistration(
                tournament,
                activeTeamCount
        );

        if (teamRepository
                .existsByTournamentIdAndCaptainId(
                        invitation.getTournamentId(),
                        currentUserId
                )) {

            throw new TeamOperationException(
                    "You already have a team in this tournament"
            );
        }

        invitation.setStatus(
                InvitationStatus.ACCEPTED
        );

        invitation.setRespondedAt(
                LocalDateTime.now()
        );

        TeamInvitation saved =
                invitationRepository.save(invitation);

        return prepareResponse(saved);
    }

    @Override
    public TeamInvitationResponse
    rejectInvitation(
            String token
    ) {

        Long currentUserId =
                SecurityUtils.requireCurrentUserId();

        TeamInvitation invitation =
                findInvitation(token);

        verifyInvitationOwner(
                invitation,
                currentUserId
        );

        refreshExpiration(invitation);

        if (invitation.getStatus() !=
                InvitationStatus.PENDING) {

            throw new TeamOperationException(
                    "Invitation is no longer pending"
            );
        }

        invitation.setStatus(
                InvitationStatus.REJECTED
        );

        invitation.setRespondedAt(
                LocalDateTime.now()
        );

        TeamInvitation saved =
                invitationRepository.save(invitation);

        return prepareResponse(saved);
    }

    private TeamInvitation findInvitation(
            String token
    ) {

        return invitationRepository
                .findByInvitationToken(token)
                .orElseThrow(() ->
                        new InvitationNotFoundException(
                                "Invitation not found"
                        )
                );
    }

    private void verifyInvitationOwner(
            TeamInvitation invitation,
            Long currentUserId
    ) {

        if (!invitation
                .getInvitedCaptainId()
                .equals(currentUserId)) {

            throw new TeamAccessDeniedException(
                    "This invitation does not belong to you"
            );
        }
    }

    private void refreshExpiration(
            TeamInvitation invitation
    ) {

        if (invitation.getStatus() ==
                InvitationStatus.PENDING &&
                !invitation.getExpiresAt()
                        .isAfter(LocalDateTime.now())) {

            invitation.setStatus(
                    InvitationStatus.EXPIRED
            );

            invitationRepository.save(invitation);
        }
    }

    private TeamInvitationResponse
    prepareResponse(
            TeamInvitation invitation
    ) {

        /*
         * We need a mutable entity here because an expired
         * pending invitation should be displayed as EXPIRED.
         *
         * This method is normally used within a transaction.
         */
        if (invitation.getStatus() ==
                InvitationStatus.PENDING &&
                !invitation.getExpiresAt()
                        .isAfter(LocalDateTime.now())) {

            invitation.setStatus(
                    InvitationStatus.EXPIRED
            );

            invitationRepository.save(invitation);
        }

        String url =
                InvitationUtil.buildInvitationUrl(
                        frontendBaseUrl,
                        invitation.getInvitationToken()
                );

        return TeamMapper.toResponse(
                invitation,
                url
        );
    }
}