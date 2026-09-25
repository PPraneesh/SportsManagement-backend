package com.praneesh.sports.team_service.service;

import com.praneesh.sports.team_service.dto.request.CreateInvitationRequest;
import com.praneesh.sports.team_service.dto.response.TeamInvitationResponse;

import java.util.List;

public interface InvitationService {

    TeamInvitationResponse createInvitation(
            Long tournamentId,
            CreateInvitationRequest request
    );

    List<TeamInvitationResponse> getTournamentInvitations(
            Long tournamentId
    );

    List<TeamInvitationResponse> getMyInvitations();

    TeamInvitationResponse acceptInvitation(
            String token
    );

    TeamInvitationResponse rejectInvitation(
            String token
    );
}