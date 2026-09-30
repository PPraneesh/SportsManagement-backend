package com.praneesh.sports.team_service.service;

import com.praneesh.sports.team_service.client.TournamentClient;
import com.praneesh.sports.team_service.client.UserClient;
import com.praneesh.sports.team_service.client.dto.InternalUserResponse;
import com.praneesh.sports.team_service.client.dto.TournamentRegistrationInfo;

import com.praneesh.sports.team_service.dto.request.AddTeamMemberRequest;
import com.praneesh.sports.team_service.dto.request.ManualTeamRequest;
import com.praneesh.sports.team_service.dto.request.RegisterTeamRequest;
import com.praneesh.sports.team_service.dto.request.UpdateTeamRequest;

import com.praneesh.sports.team_service.dto.response.InternalTeamResponse;
import com.praneesh.sports.team_service.dto.response.MyTeamResponse;
import com.praneesh.sports.team_service.dto.response.TeamMemberResponse;
import com.praneesh.sports.team_service.dto.response.TeamResponse;

import com.praneesh.sports.team_service.entity.Team;
import com.praneesh.sports.team_service.entity.TeamMember;

import com.praneesh.sports.team_service.enums.TeamMemberRole;
import com.praneesh.sports.team_service.enums.TeamStatus;

import com.praneesh.sports.team_service.exception.TeamAccessDeniedException;
import com.praneesh.sports.team_service.exception.TeamNotFoundException;
import com.praneesh.sports.team_service.exception.TeamOperationException;

import com.praneesh.sports.team_service.kafka.TeamKafkaProducer;
import com.praneesh.sports.team_service.mapper.TeamMapper;

import com.praneesh.sports.team_service.repository.TeamMemberRepository;
import com.praneesh.sports.team_service.repository.TeamRepository;
import com.praneesh.sports.team_service.repository.TeamInvitationRepository;

import com.praneesh.sports.team_service.util.SecurityUtils;
import com.praneesh.sports.team_service.util.TeamValidationUtil;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.context.ApplicationEventPublisher;

import com.praneesh.sports.team_service.dto.event.TeamCapacityReachedEvent;

import java.util.List;
import java.util.Map;

@Service
@Transactional
public class TeamServiceImpl
        implements TeamService {

    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final TeamInvitationRepository invitationRepository;

    private final TournamentClient tournamentClient;
    private final UserClient userClient;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final TeamKafkaProducer teamKafkaProducer;

    public TeamServiceImpl(
            TeamRepository teamRepository,
            TeamMemberRepository teamMemberRepository,
            TeamInvitationRepository invitationRepository,
            TournamentClient tournamentClient,
            UserClient userClient,
            ApplicationEventPublisher applicationEventPublisher,
            TeamKafkaProducer teamKafkaProducer) {

        this.teamRepository = teamRepository;
        this.teamMemberRepository = teamMemberRepository;
        this.invitationRepository = invitationRepository;

        this.tournamentClient = tournamentClient;
        this.userClient = userClient;

        this.applicationEventPublisher = applicationEventPublisher;
        this.teamKafkaProducer = teamKafkaProducer;
    }

    @Override
    public TeamResponse registerTeam(
            Long tournamentId,
            RegisterTeamRequest request
    ) {

        Long captainId =
                SecurityUtils.requireCurrentUserId();

        TournamentRegistrationInfo tournament =
                tournamentClient.getRegistrationInfo(
                        tournamentId
                );

        long activeTeamCount =
                teamRepository.countByTournamentIdAndStatus(
                        tournamentId,
                        TeamStatus.ACTIVE
                );

        TeamValidationUtil.validateRegistration(
                tournament,
                activeTeamCount
        );

        userClient.verifyActiveUser(captainId);

        ensureCaptainCanRegister(
                tournamentId,
                captainId
        );

        /*
         * PRIVATE tournaments require an accepted invitation.
         */
        if ("PRIVATE".equals(tournament.visibility())) {

            boolean invited =
                    invitationRepository
                            .existsByTournamentIdAndInvitedCaptainIdAndStatus(
                                    tournamentId,
                                    captainId,
                                    com.praneesh.sports.team_service.enums.InvitationStatus.ACCEPTED
                            );

            if (!invited) {

                throw new TeamOperationException(
                        "You need an accepted invitation to register for this private tournament"
                );
            }
        }

        Team team =
                TeamMapper.toEntity(
                        request,
                        tournamentId,
                        captainId
                );

        Team savedTeam =
                teamRepository.saveAndFlush(team);

        TeamMember captain =
                TeamMapper.toCaptainMember(
                        savedTeam.getId(),
                        captainId
                );

        teamMemberRepository.save(captain);

        long newTeamCount =
                activeTeamCount + 1;

        /*
         * Best-effort automatic close when capacity is reached.
         */
        if (newTeamCount >=
                tournament.maximumTeams()) {

            applicationEventPublisher.publishEvent(
                    new TeamCapacityReachedEvent(
                            tournamentId,
                            newTeamCount
                    )
            );
        }

        return TeamMapper.toResponse(savedTeam);
    }

    @Override
    public TeamResponse manuallyCreateTeam(
            Long tournamentId,
            ManualTeamRequest request
    ) {

        Long organizerId =
                SecurityUtils.requireCurrentUserId();

        TournamentRegistrationInfo tournament =
                tournamentClient.getRegistrationInfo(
                        tournamentId
                );

        verifyOrganizer(
                tournament,
                organizerId
        );

        long activeTeamCount =
                teamRepository.countByTournamentIdAndStatus(
                        tournamentId,
                        TeamStatus.ACTIVE
                );

        TeamValidationUtil.validateRegistration(
                tournament,
                activeTeamCount
        );

        InternalUserResponse captain  = userClient.verifyActiveUserByEmail(
                request.captainEmail()
        );
        Long captainId = captain.id();

        ensureCaptainCanRegister(
                tournamentId,
                captainId
        );

        Team team = TeamMapper.toEntity(
                        request,
                        tournamentId,
                        captainId
                );

        Team savedTeam =
                teamRepository.saveAndFlush(team);

        TeamMember captainMember =
                TeamMapper.toCaptainMember(
                        savedTeam.getId(),
                        captainId
                );

        teamMemberRepository.save(captainMember);

        long newTeamCount =
                activeTeamCount + 1;

        if (newTeamCount >=
                tournament.maximumTeams()) {

            applicationEventPublisher.publishEvent(
                    new TeamCapacityReachedEvent(
                            tournamentId,
                            newTeamCount
                    )
            );
        }

        return TeamMapper.toResponse(savedTeam);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TeamResponse> getTournamentTeams(
            Long tournamentId
    ) {

        TournamentRegistrationInfo tournament =
                tournamentClient.getRegistrationInfo(
                        tournamentId
                );

        verifyTournamentViewAccess(
                tournament
        );

        return teamRepository
                .findAllByTournamentIdOrderByCreatedAtAsc(
                        tournamentId
                )
                .stream()
                .map(TeamMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public TeamResponse getTeamById(
            Long teamId
    ) {

        Team team =
                findTeam(teamId);

        TournamentRegistrationInfo tournament =
                tournamentClient.getRegistrationInfo(
                        team.getTournamentId()
                );

        verifyTournamentViewAccess(
                tournament
        );

        return TeamMapper.toResponse(team);
    }

    @Override
    @Transactional(readOnly = true)
    public TeamResponse getMyTeam(
            Long tournamentId
    ) {

        Long userId =
                SecurityUtils.requireCurrentUserId();

        List<Team> teams =
                teamRepository
                        .findAllByTournamentIdAndStatusOrderByCreatedAtAsc(
                                tournamentId,
                                TeamStatus.ACTIVE
                        );

        for (Team team : teams) {

            if (team.getCaptainId().equals(userId)) {
                return TeamMapper.toResponse(team);
            }

            if (teamMemberRepository
                    .existsByTeamIdAndUserIdAndActiveTrue(
                            team.getId(),
                            userId
                    )) {

                return TeamMapper.toResponse(team);
            }
        }

        throw new TeamNotFoundException(
                "You are not a member of any active team in this tournament"
        );
    }

    @Override
    public TeamResponse updateTeam(
            Long teamId,
            UpdateTeamRequest request
    ) {

        Team team =
                findTeam(teamId);

        Long currentUserId =
                SecurityUtils.requireCurrentUserId();

        verifyCaptain(
                team,
                currentUserId
        );

        if (team.getStatus() ==
                TeamStatus.WITHDRAWN) {

            throw new TeamOperationException(
                    "A withdrawn team cannot be modified"
            );
        }

        TournamentRegistrationInfo tournament =
                tournamentClient.getRegistrationInfo(
                        team.getTournamentId()
                );

        TeamValidationUtil.validateTeamModifiable(
                tournament
        );

        TeamMapper.updateEntity(
                team,
                request
        );

        return TeamMapper.toResponse(
                teamRepository.save(team)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<TeamMemberResponse> getMembers(
            Long teamId
    ) {

        Team team =
                findTeam(teamId);

        TournamentRegistrationInfo tournament =
                tournamentClient.getRegistrationInfo(
                        team.getTournamentId()
                );

        verifyTournamentViewAccess(
                tournament
        );

        return teamMemberRepository
                .findAllByTeamIdAndActiveTrueOrderByJoinedAtAsc(
                        teamId
                )
                .stream()
                .map(member -> {

                    InternalUserResponse user =
                            userClient.getUser(
                                    member.getUserId()
                            );

                    return TeamMapper.toResponse(
                            member,
                            user
                    );
                })
                .toList();
    }

    @Override
    public TeamMemberResponse addMember(
            Long teamId,
            AddTeamMemberRequest request
    ) {

        Team team =
                findTeam(teamId);

        Long currentUserId =
                SecurityUtils.requireCurrentUserId();

        verifyCaptain(
                team,
                currentUserId
        );

        if (team.getStatus() ==
                TeamStatus.WITHDRAWN) {

            throw new TeamOperationException(
                    "A withdrawn team cannot add members"
            );
        }

        TournamentRegistrationInfo tournament =
                tournamentClient.getRegistrationInfo(
                        team.getTournamentId()
                );

        TeamValidationUtil.validateTeamModifiable(
                tournament
        );

        InternalUserResponse member = userClient.verifyActiveUserByEmail(
                request.email()
        );

        if (teamMemberRepository
                .existsByTeamIdAndUserIdAndActiveTrue(
                        teamId,
                        member.id()
                )) {

            throw new TeamOperationException(
                    "User is already a member of this team"
            );
        }

        List<Long> activeTeamIds =
                teamRepository
                        .findAllByTournamentIdAndStatusOrderByCreatedAtAsc(
                                team.getTournamentId(),
                                TeamStatus.ACTIVE
                        )
                        .stream()
                        .map(Team::getId)
                        .toList();

        if (!activeTeamIds.isEmpty() &&
                teamMemberRepository
                        .existsByTeamIdInAndUserIdAndActiveTrue(
                                activeTeamIds,
                                member.id()
                        )) {

            throw new TeamOperationException(
                    "A user can belong to only one team in a tournament"
            );
        }

        TeamMember teamMember =
                TeamMapper.toPlayerMember(
                        teamId,
                        member.id()
                );

        return TeamMapper.toResponse(
                teamMemberRepository.save(teamMember),
                member
        );
    }

    @Override
    public void removeMember(
            Long teamId,
            Long memberId
    ) {

        Team team =
                findTeam(teamId);

        Long currentUserId =
                SecurityUtils.requireCurrentUserId();

        verifyCaptain(
                team,
                currentUserId
        );

        TournamentRegistrationInfo tournament =
                tournamentClient.getRegistrationInfo(
                        team.getTournamentId()
                );

        TeamValidationUtil.validateTeamModifiable(
                tournament
        );

        TeamMember member =
                teamMemberRepository
                        .findByIdAndTeamId(
                                memberId,
                                teamId
                        )
                        .orElseThrow(() ->
                                new TeamOperationException(
                                        "Team member not found"
                                )
                        );

        if (!member.isActive()) {

            throw new TeamOperationException(
                    "Team member is already inactive"
            );
        }

        if (member.getMemberRole() ==
                TeamMemberRole.CAPTAIN) {

            throw new TeamOperationException(
                    "Captain cannot be removed. Transfer captaincy first."
            );
        }

        member.setActive(false);

        teamMemberRepository.save(member);
    }

    @Override
    public TeamResponse withdrawTeam(
            Long teamId
    ) {

        Team team =
                findTeam(teamId);

        Long currentUserId =
                SecurityUtils.requireCurrentUserId();

        TournamentRegistrationInfo tournament =
                tournamentClient.getRegistrationInfo(
                        team.getTournamentId()
                );

        boolean isCaptain =
                team.getCaptainId().equals(
                        currentUserId
                );

        boolean isOrganizer =
                tournament.organizerId().equals(
                        currentUserId
                );

        if (!isCaptain && !isOrganizer) {

            throw new TeamAccessDeniedException(
                    "Only the team captain or tournament organizer can withdraw the team"
            );
        }

        String status = tournament.status();

        if ("COMPLETED".equals(status) ||
                "CANCELLED".equals(status)) {

            throw new TeamOperationException(
                    "A team cannot be withdrawn after tournament completion or cancellation"
            );
        }

        if (team.getStatus() ==
                TeamStatus.WITHDRAWN) {

            throw new TeamOperationException(
                    "Team is already withdrawn"
            );
        }

        team.setStatus(
                TeamStatus.WITHDRAWN
        );

        List<TeamMember> members =
                teamMemberRepository
                        .findAllByTeamIdAndActiveTrueOrderByJoinedAtAsc(
                                teamId
                        );

        members.forEach(member ->
                member.setActive(false)
        );

        teamMemberRepository.saveAll(members);

        teamKafkaProducer.publishTeamWithdraw(tournament.id(),teamId);
        return TeamMapper.toResponse(
                teamRepository.save(team)
        );
    }

    private Team findTeam(
            Long teamId
    ) {

        return teamRepository.findById(teamId)
                .orElseThrow(() ->
                        new TeamNotFoundException(
                                "Team not found with id: " + teamId
                        )
                );
    }

    private void ensureCaptainCanRegister(
            Long tournamentId,
            Long captainId
    ) {

        /*
         * One team per captain per tournament.
         * This includes a previously withdrawn team.
         */
        if (teamRepository
                .existsByTournamentIdAndCaptainId(
                        tournamentId,
                        captainId
                )) {

            throw new TeamOperationException(
                    "You already have a team in this tournament"
            );
        }

        List<Long> activeTeamIds =
                teamRepository
                        .findAllByTournamentIdAndStatusOrderByCreatedAtAsc(
                                tournamentId,
                                TeamStatus.ACTIVE
                        )
                        .stream()
                        .map(Team::getId)
                        .toList();

        if (!activeTeamIds.isEmpty() &&
                teamMemberRepository
                        .existsByTeamIdInAndUserIdAndActiveTrue(
                                activeTeamIds,
                                captainId
                        )) {

            throw new TeamOperationException(
                    "You are already a member of another team in this tournament"
            );
        }
    }

    private void verifyOrganizer(
            TournamentRegistrationInfo tournament,
            Long currentUserId
    ) {

        if (!tournament.organizerId()
                .equals(currentUserId)) {

            throw new TeamAccessDeniedException(
                    "Only the tournament organizer can perform this operation"
            );
        }
    }

    private void verifyCaptain(
            Team team,
            Long currentUserId
    ) {

        if (!team.getCaptainId()
                .equals(currentUserId)) {

            throw new TeamAccessDeniedException(
                    "Only the team captain can perform this operation"
            );
        }
    }

    private void verifyTournamentViewAccess(
            TournamentRegistrationInfo tournament
    ) {

        /*
         * Public tournaments can be viewed by anyone.
         */
        if ("PUBLIC".equals(tournament.visibility())) {
            return;
        }

        /*
         * Private tournaments require authentication.
         */
        Long currentUserId =
                SecurityUtils.requireCurrentUserId();

        /*
         * Organizer is always allowed.
         */
        if (tournament.organizerId()
                .equals(currentUserId)) {
            return;
        }

        /*
         * Accepted invitation.
         */
        boolean acceptedInvitation =
                invitationRepository
                        .existsByTournamentIdAndInvitedCaptainIdAndStatus(
                                tournament.id(),
                                currentUserId,
                                com.praneesh.sports.team_service.enums.InvitationStatus.ACCEPTED
                        );

        if (acceptedInvitation) {
            return;
        }

        /*
         * An active team member is allowed as well.
         */
        List<Long> teamIds =
                teamRepository
                        .findAllByTournamentIdOrderByCreatedAtAsc(
                                tournament.id()
                        )
                        .stream()
                        .map(Team::getId)
                        .toList();

        if (!teamIds.isEmpty() &&
                teamMemberRepository
                        .existsByTeamIdInAndUserIdAndActiveTrue(
                                teamIds,
                                currentUserId
                        )) {

            return;
        }

        throw new TeamAccessDeniedException(
                "You do not have access to this private tournament"
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<InternalTeamResponse> getInternalTournamentTeams(
            Long tournamentId
    ) {

        List<Team> teams =
                teamRepository
                        .findAllByTournamentIdAndStatusOrderByCreatedAtAsc(
                                tournamentId,
                                TeamStatus.ACTIVE
                        );

        return java.util.stream.IntStream
                .range(0, teams.size())
                .mapToObj(index ->
                        TeamMapper.toInternalResponse(
                                teams.get(index),
                                index + 1
                        )
                )
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MyTeamResponse> getMyTeams() {

        Long userId =
                SecurityUtils.requireCurrentUserId();

        List<TeamMember> memberships =
                teamMemberRepository
                        .findAllByUserIdAndActiveTrueOrderByJoinedAtDesc(
                                userId
                        );

        if (memberships.isEmpty()) {
            return List.of();
        }

        List<Long> teamIds =
                memberships.stream()
                        .map(TeamMember::getTeamId)
                        .toList();

        Map<Long, Team> teamsById =
                teamRepository.findAllById(teamIds)
                        .stream()
                        .collect(
                                java.util.stream.Collectors.toMap(
                                        Team::getId,
                                        team -> team
                                )
                        );

        return memberships.stream()
                .map(member -> {

                    Team team =
                            teamsById.get(
                                    member.getTeamId()
                            );

                    if (team == null) {
                        return null;
                    }

                    return TeamMapper.toMyTeamResponse(
                            team,
                            member
                    );
                })
                .filter(java.util.Objects::nonNull)
                .toList();
    }
}