package com.praneesh.sports.team_service.service;

import com.praneesh.sports.team_service.dto.request.AddTeamMemberRequest;
import com.praneesh.sports.team_service.dto.request.ManualTeamRequest;
import com.praneesh.sports.team_service.dto.request.RegisterTeamRequest;
import com.praneesh.sports.team_service.dto.request.UpdateTeamRequest;

import com.praneesh.sports.team_service.dto.response.InternalTeamResponse;
import com.praneesh.sports.team_service.dto.response.MyTeamResponse;
import com.praneesh.sports.team_service.dto.response.TeamMemberResponse;
import com.praneesh.sports.team_service.dto.response.TeamResponse;

import java.util.List;

public interface TeamService {

    TeamResponse registerTeam(
            Long tournamentId,
            RegisterTeamRequest request
    );

    TeamResponse manuallyCreateTeam(
            Long tournamentId,
            ManualTeamRequest request
    );

    List<TeamResponse> getTournamentTeams(
            Long tournamentId
    );

    TeamResponse getTeamById(
            Long teamId
    );

    TeamResponse getMyTeam(
            Long tournamentId
    );

    TeamResponse updateTeam(
            Long teamId,
            UpdateTeamRequest request
    );

    List<TeamMemberResponse> getMembers(
            Long teamId
    );

    TeamMemberResponse addMember(
            Long teamId,
            AddTeamMemberRequest request
    );

    void removeMember(
            Long teamId,
            Long memberId
    );

    TeamResponse withdrawTeam(
            Long teamId
    );

    List<InternalTeamResponse> getInternalTournamentTeams(
            Long tournamentId
    );
    List<MyTeamResponse> getMyTeams();
}