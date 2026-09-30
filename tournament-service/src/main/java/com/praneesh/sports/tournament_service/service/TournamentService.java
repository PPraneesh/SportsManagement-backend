package com.praneesh.sports.tournament_service.service;


import com.praneesh.sports.tournament_service.dto.request.CreateTournamentRequest;
import com.praneesh.sports.tournament_service.dto.request.UpdateTournamentRequest;
import com.praneesh.sports.tournament_service.dto.response.InternalTournamentRegistrationResponse;
import com.praneesh.sports.tournament_service.dto.response.TournamentResponse;

import java.util.List;

public interface TournamentService {

    TournamentResponse createTournament(
            CreateTournamentRequest request
    );

    TournamentResponse getTournamentById(
            Long tournamentId
    );

    TournamentResponse getTournamentBySlug(
            String slug
    );

    List<TournamentResponse> getPublicTournaments();

    List<TournamentResponse> getMyTournaments();

    TournamentResponse updateTournament(
            Long tournamentId,
            UpdateTournamentRequest request
    );

    TournamentResponse openRegistration(
            Long tournamentId
    );

    TournamentResponse closeRegistration(
            Long tournamentId
    );

    TournamentResponse cancelTournament(
            Long tournamentId
    );

    InternalTournamentRegistrationResponse getRegistrationInfo(Long tournamentId);

    void completeTournament(
            Long tournamentId
    );

    void closeRegistrationIfCapacityReached(
            Long tournamentId,
            long activeTeamCount
    );
}