package com.praneesh.sports.team_service.mapper;

import com.praneesh.sports.team_service.client.dto.InternalUserResponse;
import com.praneesh.sports.team_service.dto.request.CreateInvitationRequest;
import com.praneesh.sports.team_service.dto.request.ManualTeamRequest;
import com.praneesh.sports.team_service.dto.request.RegisterTeamRequest;
import com.praneesh.sports.team_service.dto.request.UpdateTeamRequest;

import com.praneesh.sports.team_service.dto.response.*;

import com.praneesh.sports.team_service.entity.Team;
import com.praneesh.sports.team_service.entity.TeamInvitation;
import com.praneesh.sports.team_service.entity.TeamMember;

import com.praneesh.sports.team_service.enums.InvitationStatus;
import com.praneesh.sports.team_service.enums.TeamMemberRole;
import com.praneesh.sports.team_service.enums.TeamStatus;

import java.time.LocalDateTime;
import java.util.Locale;

public final class TeamMapper {

    private TeamMapper() {
    }

    public static Team toEntity(
            RegisterTeamRequest request,
            Long tournamentId,
            Long captainId
    ) {

        Team team = new Team();

        team.setTournamentId(tournamentId);
        team.setCaptainId(captainId);
        team.setName(request.name().trim());
        team.setShortName(request.shortName().trim().toUpperCase(
                Locale.ROOT
        ));
        team.setLogoUrl(trimToNull(request.logoUrl()));
        team.setDescription(trimToNull(request.description()));
        team.setStatus(TeamStatus.ACTIVE);

        return team;
    }

    public static Team toEntity(
            ManualTeamRequest request,
            Long tournamentId,
            Long captainId
    ) {

        Team team = new Team();

        team.setTournamentId(tournamentId);
        team.setCaptainId(captainId);
        team.setName(request.name().trim());
        team.setShortName(request.shortName().trim().toUpperCase(
                Locale.ROOT
        ));
        team.setLogoUrl(trimToNull(request.logoUrl()));
        team.setDescription(trimToNull(request.description()));
        team.setStatus(TeamStatus.ACTIVE);

        return team;
    }

    public static void updateEntity(
            Team team,
            UpdateTeamRequest request
    ) {

        team.setName(request.name().trim());

        team.setShortName(
                request.shortName()
                        .trim()
                        .toUpperCase(Locale.ROOT)
        );

        team.setLogoUrl(
                trimToNull(request.logoUrl())
        );

        team.setDescription(
                trimToNull(request.description())
        );
    }

    public static TeamResponse toResponse(
            Team team
    ) {

        return new TeamResponse(
                team.getId(),
                team.getTournamentId(),
                team.getCaptainId(),
                team.getName(),
                team.getShortName(),
                team.getLogoUrl(),
                team.getDescription(),
                team.getStatus(),
                team.getCreatedAt(),
                team.getUpdatedAt()
        );
    }

    public static TeamMember toCaptainMember(
            Long teamId,
            Long captainId
    ) {

        TeamMember member = new TeamMember();

        member.setTeamId(teamId);
        member.setUserId(captainId);
        member.setMemberRole(
                TeamMemberRole.CAPTAIN
        );
        member.setActive(true);

        return member;
    }

    public static TeamMember toPlayerMember(
            Long teamId,
            Long userId
    ) {

        TeamMember member = new TeamMember();

        member.setTeamId(teamId);
        member.setUserId(userId);
        member.setMemberRole(
                TeamMemberRole.PLAYER
        );
        member.setActive(true);

        return member;
    }

    public static TeamInvitation toEntity(
            CreateInvitationRequest request,
            Long tournamentId,
            Long invitedCaptainId,
            String token,
            LocalDateTime expiresAt
    ) {

        TeamInvitation invitation = new TeamInvitation();

        invitation.setTournamentId(tournamentId);
        invitation.setInvitedCaptainId(
                invitedCaptainId
        );
        invitation.setInvitationToken(token);
        invitation.setStatus(
                InvitationStatus.PENDING
        );
        invitation.setExpiresAt(expiresAt);

        return invitation;
    }

    public static TeamInvitationResponse
    toResponse(
            TeamInvitation invitation,
            String invitationUrl
    ) {

        return new TeamInvitationResponse(
                invitation.getId(),
                invitation.getTournamentId(),
                invitation.getInvitedCaptainId(),
                invitation.getStatus(),
                invitationUrl,
                invitation.getExpiresAt(),
                invitation.getRespondedAt(),
                invitation.getCreatedAt()
        );
    }

    private static String trimToNull(
            String value
    ) {

        if (value == null) {
            return null;
        }

        String trimmed = value.trim();

        return trimmed.isEmpty() ? null : trimmed;
    }

    public static InternalTeamResponse toInternalResponse(
            Team team,
            Integer registrationOrder
    ) {

        return new InternalTeamResponse(
                team.getId(),
                team.getTournamentId(),
                team.getName(),
                team.getShortName(),
                team.getStatus().name(),
                team.getCreatedAt(),
                registrationOrder
        );
    }

    public static TeamMemberResponse toResponse(
            TeamMember member,
            InternalUserResponse user
    ) {

        return new TeamMemberResponse(
                member.getId(),
                member.getTeamId(),
                user.name(),
                user.email(),
                member.getMemberRole(),
                member.isActive(),
                member.getJoinedAt()
        );
    }

    public static MyTeamResponse toMyTeamResponse(
            Team team,
            TeamMember member
    ) {

        return new MyTeamResponse(
                team.getId(),
                team.getTournamentId(),
                team.getName(),
                team.getShortName(),
                team.getLogoUrl(),
                team.getStatus(),
                member.getMemberRole()
        );
    }
}