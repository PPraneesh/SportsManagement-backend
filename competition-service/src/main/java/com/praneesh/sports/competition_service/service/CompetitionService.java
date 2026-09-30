package com.praneesh.sports.competition_service.service;

import com.praneesh.sports.competition_service.dto.request.CompleteMatchRequest;
import com.praneesh.sports.competition_service.dto.request.RescheduleMatchRequest;
import com.praneesh.sports.competition_service.dto.response.*;

import java.util.List;

public interface CompetitionService {

    List<MatchResponse> generateFixtures(
            Long tournamentId
    );

    List<MatchResponse> getTournamentMatches(
            Long tournamentId
    );

    MatchResponse getMatch(
            Long matchId
    );

    MatchResponse startMatch(
            Long matchId
    );

    MatchResponse completeMatch(
            Long matchId,
            CompleteMatchRequest request
    );

    MatchResponse postponeMatch(
            Long matchId
    );

    MatchResponse rescheduleMatch(
            Long matchId,
            RescheduleMatchRequest request
    );

    List<GroupResponse> getTournamentGroups(
            Long tournamentId
    );

    List<StandingResponse> getGroupStandings(
            Long groupId
    );

    PublicTournamentStatsResponse getPublicTournamentStats(
            String slug
    );

    PublicMatchResponse getPublicMatch(
            String slug,
            String matchCode
    );

    void handleTeamWithdrawal(
            Long tournamentId,
            Long teamId
    );
}