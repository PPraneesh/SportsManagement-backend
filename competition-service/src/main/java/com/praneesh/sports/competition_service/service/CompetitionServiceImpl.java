package com.praneesh.sports.competition_service.service;

import com.praneesh.sports.competition_service.client.TeamClient;
import com.praneesh.sports.competition_service.client.TournamentClient;
import com.praneesh.sports.competition_service.client.dto.InternalTeamResponse;

import com.praneesh.sports.competition_service.client.dto.PublicTournamentInfo;
import com.praneesh.sports.competition_service.client.dto.TournamentRegistrationInfo;
import com.praneesh.sports.competition_service.dto.common.StandingSnapshot;
import com.praneesh.sports.competition_service.dto.request.CompleteMatchRequest;
import com.praneesh.sports.competition_service.dto.request.RescheduleMatchRequest;
import com.praneesh.sports.competition_service.dto.response.*;

import com.praneesh.sports.competition_service.entity.Group;
import com.praneesh.sports.competition_service.entity.GroupTeam;
import com.praneesh.sports.competition_service.entity.Match;

import com.praneesh.sports.competition_service.enums.GroupStatus;
import com.praneesh.sports.competition_service.enums.MatchResultType;
import com.praneesh.sports.competition_service.enums.MatchStatus;
import com.praneesh.sports.competition_service.enums.MatchType;

import com.praneesh.sports.competition_service.exception.MatchAccessDeniedException;
import com.praneesh.sports.competition_service.exception.MatchNotFoundException;
import com.praneesh.sports.competition_service.exception.TournamentFixtureException;
import com.praneesh.sports.competition_service.kafka.CompetitionKafkaProducer;
import com.praneesh.sports.competition_service.mapper.GroupMapper;
import com.praneesh.sports.competition_service.mapper.MatchMapper;

import com.praneesh.sports.competition_service.repository.GroupRepository;
import com.praneesh.sports.competition_service.repository.GroupTeamRepository;
import com.praneesh.sports.competition_service.repository.MatchRepository;

import com.praneesh.sports.competition_service.util.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import com.praneesh.sports.competition_service.exception.MatchOperationException;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

import com.praneesh.sports.competition_service.dto.response.GroupResponse;
import com.praneesh.sports.competition_service.dto.response.PublicGroupResponse;
import com.praneesh.sports.competition_service.dto.response.PublicMatchResponse;
import com.praneesh.sports.competition_service.dto.response.PublicTournamentStatsResponse;
import com.praneesh.sports.competition_service.dto.response.StandingResponse;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@Transactional
public class CompetitionServiceImpl implements CompetitionService {

    private final MatchRepository matchRepository;

    private final TeamClient teamClient;
    private final TournamentClient tournamentClient;
    private final GroupRepository groupRepository;
    private final GroupTeamRepository groupTeamRepository;
    private final CompetitionKafkaProducer competitionKafkaProducer;

    //    private final TournamentClient tournamentClient;
    public CompetitionServiceImpl(MatchRepository matchRepository, TeamClient teamClient, TournamentClient tournamentClient, GroupRepository groupRepository, GroupTeamRepository groupTeamRepository, CompetitionKafkaProducer competitionKafkaProducer) {

        this.matchRepository = matchRepository;

        this.teamClient = teamClient;

        this.tournamentClient = tournamentClient;

        this.groupRepository = groupRepository;
        this.groupTeamRepository = groupTeamRepository;

        this.competitionKafkaProducer = competitionKafkaProducer;
    }

    @Override
    @Transactional
    public List<MatchResponse> generateFixtures(Long tournamentId) {

        if (matchRepository.existsByTournamentId(tournamentId)) {

            throw new TournamentFixtureException("Fixtures have already been generated for this tournament");
        }

        List<InternalTeamResponse> teams = teamClient.getTournamentTeams(tournamentId);

        if (teams.size() < 4) {

            throw new TournamentFixtureException("At least 4 teams are required to generate fixtures");
        }

        /*
         * 4 teams → Direct Knockout
         */
        if (teams.size() == 4) {

            return generateDirectKnockoutFixtures(tournamentId, teams);
        }

        /*
         * 5+ teams → Group Stage
         */
        return generateGroupStageFixtures(tournamentId, teams);
    }

    private List<Group> createGroups(Long tournamentId, int groupCount) {

        List<Group> groups = new ArrayList<>();

        for (int i = 0; i < groupCount; i++) {

            char letter = (char) ('A' + i);

            groups.add(GroupMapper.toEntity(tournamentId, "Group " + letter, i + 1));
        }

        return groups;
    }

    private List<Match> createGroupMatches(List<Group> groups, List<GroupTeam> groupTeams, Long tournamentId) {

        List<Match> matches = new ArrayList<>();

        int globalMatchNumber = 1;

        for (Group group : groups) {

            List<GroupTeam> teamsInGroup = groupTeams.stream().filter(groupTeam -> groupTeam.getGroupId().equals(group.getId())).sorted(Comparator.comparing(GroupTeam::getSeedNumber)).toList();

            for (int i = 0; i < teamsInGroup.size(); i++) {

                for (int j = i + 1; j < teamsInGroup.size(); j++) {

                    GroupTeam teamA = teamsInGroup.get(i);

                    GroupTeam teamB = teamsInGroup.get(j);

                    Match match = new Match();

                    match.setTournamentId(tournamentId);

                    match.setGroupId(group.getId());

                    match.setTeamAId(teamA.getTeamId());

                    match.setTeamBId(teamB.getTeamId());

                    match.setMatchCode(group.getName().replace(" ", "") + "-M" + (globalMatchNumber));

                    match.setMatchType(MatchType.GROUP_STAGE);

                    match.setStatus(MatchStatus.SCHEDULED);

                    match.setRoundNumber(1);

                    match.setMatchNumber(globalMatchNumber);

                    matches.add(match);

                    globalMatchNumber++;
                }
            }
        }

        return matches;
    }

    private List<GroupTeam> allocateTeamsToGroups(List<Group> groups, List<InternalTeamResponse> teams) {

        List<GroupTeam> result = new ArrayList<>();

        int teamCount = teams.size();

        int groupCount = groups.size();

        int baseSize = teamCount / groupCount;

        int remainder = teamCount % groupCount;

        int teamIndex = 0;

        for (int groupIndex = 0; groupIndex < groupCount; groupIndex++) {

            Group group = groups.get(groupIndex);

            int groupSize = baseSize + (groupIndex < remainder ? 1 : 0);

            for (int seed = 1; seed <= groupSize; seed++) {

                InternalTeamResponse team = teams.get(teamIndex++);

                GroupTeam groupTeam = new GroupTeam();

                groupTeam.setGroupId(group.getId());

                groupTeam.setTeamId(team.id());

                groupTeam.setSeedNumber(seed);

                result.add(groupTeam);
            }
        }

        return result;
    }

    private List<MatchResponse> generateGroupStageFixtures(Long tournamentId, List<InternalTeamResponse> teams) {

        int teamCount = teams.size();

        int groupCount = Math.min(4, teamCount / 2);

        /*
         * Example:
         *
         * 5 → 2 groups
         * 6 → 3 groups
         * 7 → 3 groups
         * 8 → 4 groups
         */

        List<Group> groups = createGroups(tournamentId, groupCount);

        groupRepository.saveAll(groups);

        List<GroupTeam> groupTeams = allocateTeamsToGroups(groups, teams);

        groupTeamRepository.saveAll(groupTeams);

        List<Match> matches = createGroupMatches(groups, groupTeams, tournamentId);

        matchRepository.saveAll(matches);

        return matchRepository.findAllByTournamentIdOrderByRoundNumberAscMatchNumberAsc(tournamentId).stream().map(MatchMapper::toResponse).toList();
    }

    private List<MatchResponse> generateDirectKnockoutFixtures(Long tournamentId, List<InternalTeamResponse> teams) {

        InternalTeamResponse team1 = teams.get(0);

        InternalTeamResponse team2 = teams.get(1);

        InternalTeamResponse team3 = teams.get(2);

        InternalTeamResponse team4 = teams.get(3);

        Match semifinal1 = createSemifinal(tournamentId, team1.id(), team2.id(), "SF1", 1, 1);

        Match semifinal2 = createSemifinal(tournamentId, team3.id(), team4.id(), "SF2", 1, 2);

        Match finalMatch = createFinal(tournamentId);

        matchRepository.saveAll(List.of(semifinal1, semifinal2, finalMatch));

        return matchRepository.findAllByTournamentIdOrderByRoundNumberAscMatchNumberAsc(tournamentId).stream().map(MatchMapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MatchResponse> getTournamentMatches(Long tournamentId) {

        return matchRepository.findAllByTournamentIdOrderByRoundNumberAscMatchNumberAsc(tournamentId).stream().map(MatchMapper::toResponse).toList();
    }

    private Match createSemifinal(Long tournamentId, Long teamAId, Long teamBId, String matchCode, Integer roundNumber, Integer matchNumber) {

        Match match = new Match();

        match.setTournamentId(tournamentId);

        match.setTeamAId(teamAId);

        match.setTeamBId(teamBId);

        match.setMatchCode(matchCode);

        match.setMatchType(MatchType.SEMIFINAL);

        match.setStatus(MatchStatus.SCHEDULED);

        match.setRoundNumber(roundNumber);

        match.setMatchNumber(matchNumber);

        return match;
    }

    private Match createFinal(Long tournamentId) {

        Match match = new Match();

        match.setTournamentId(tournamentId);

        /*
         * Final participants are populated later
         * after the semifinals are completed.
         */
        match.setTeamAId(null);

        match.setTeamBId(null);

        match.setWinnerTeamId(null);

        match.setMatchCode("FINAL");

        match.setMatchType(MatchType.FINAL);

        match.setStatus(MatchStatus.SCHEDULED);

        match.setRoundNumber(2);

        match.setMatchNumber(1);

        return match;
    }

    @Override
    public MatchResponse startMatch(Long matchId) {

        Match match = findMatch(matchId);

        verifyOrganizer(match.getTournamentId());

        if (match.getStatus() != MatchStatus.SCHEDULED) {

            throw new MatchOperationException("Only a scheduled match can be started");
        }

        if (match.getTeamAId() == null || match.getTeamBId() == null) {

            throw new MatchOperationException("Both teams must be assigned before the match can start");
        }

        match.setStatus(MatchStatus.LIVE);

        match.setStartedAt(LocalDateTime.now());

        Match savedMatch = matchRepository.save(match);

        return MatchMapper.toResponse(savedMatch);
    }

    @Override
    public MatchResponse completeMatch(Long matchId, CompleteMatchRequest request) {

        Match match = findMatch(matchId);

        verifyOrganizer(match.getTournamentId());

        if (match.getStatus() != MatchStatus.LIVE) {

            throw new MatchOperationException("Only a live match can be completed");
        }

        if (match.getTeamAId() == null || match.getTeamBId() == null) {

            throw new MatchOperationException("Both teams must be assigned before completing the match");
        }

        Long winnerTeamId = determineWinner(match, request);

        match.setTeamAScore(request.teamAScore());
        match.setTeamBScore(request.teamBScore());

        match.setTeamARunRate(request.teamARunRate());
        match.setTeamBRunRate(request.teamBRunRate());

        if (request.winnerTeamId() != null
                && !request.winnerTeamId().equals(winnerTeamId)) {

            throw new MatchOperationException(
                    "Provided winner does not match the calculated winner."
            );
        }

        match.setTieBreakerDescription(
                request.winnerTeamId() != null
                        ? request.tieBreakerDescription()
                        : null
        );

        match.setWinnerTeamId(winnerTeamId);

        match.setStatus(MatchStatus.COMPLETED);

        match.setCompletedAt(LocalDateTime.now());

        Match savedMatch = matchRepository.save(match);

        /*
         * Knockout progression.
         */
        if (match.getMatchType() == MatchType.GROUP_STAGE) {

            processGroupStageCompletion(match.getTournamentId());
        }

        if (match.getMatchType() == MatchType.SEMIFINAL) {

            updateFinalWithSemifinalWinner(savedMatch);
        }

        if (match.getMatchType() == MatchType.FINAL) {

            if (savedMatch.getWinnerTeamId() == null) {

                throw new MatchOperationException(
                        "Final must have a winner"
                );
            }

            competitionKafkaProducer
                    .publishTournamentCompleted(
                            savedMatch.getTournamentId()
                    );
        }


        return MatchMapper.toResponse(savedMatch);
    }

    @Override
    @Transactional(readOnly = true)
    public MatchResponse getMatch(Long matchId) {

        return MatchMapper.toResponse(findMatch(matchId));
    }

    private void processGroupStageCompletion(Long tournamentId) {

        List<Match> matches = matchRepository.findAllByTournamentIdOrderByRoundNumberAscMatchNumberAsc(tournamentId);

        List<Match> groupMatches = matches.stream().filter(match -> match.getMatchType() == MatchType.GROUP_STAGE).toList();

        if (groupMatches.isEmpty()) {
            return;
        }

        boolean allCompleted = groupMatches.stream().allMatch(match -> match.getStatus() == MatchStatus.COMPLETED);

        if (!allCompleted) {
            return;
        }

        /*
         * Prevent generating knockout twice.
         */
        boolean knockoutAlreadyGenerated = matches.stream().anyMatch(match -> match.getMatchType() == MatchType.SEMIFINAL || match.getMatchType() == MatchType.FINAL);

        if (knockoutAlreadyGenerated) {
            return;
        }

        generateKnockoutFromGroups(tournamentId);
    }

    private void generateKnockoutFromGroups(Long tournamentId) {

        TournamentRegistrationInfo tournament = tournamentClient.getRegistrationInfo(tournamentId);

        List<Group> groups = groupRepository.findAllByTournamentIdOrderBySequenceNumberAsc(tournamentId);

        Map<Long, List<StandingSnapshot>> standingsByGroup = new LinkedHashMap<>();

        for (Group group : groups) {

            List<StandingSnapshot> standings = calculateGroupStandings(group, tournament);

            ensureGroupRankingIsResolved(standings);

            standingsByGroup.put(group.getId(), standings);
        }

        List<StandingSnapshot> groupWinners = standingsByGroup.values().stream().map(list -> list.get(0)).toList();

        /*
         * 5 teams:
         *
         * 2 group winners
         * → Final
         */
        if (groups.size() == 2) {

            Match finalMatch = createFinal(tournamentId);

            finalMatch.setTeamAId(groupWinners.get(0).teamId());

            finalMatch.setTeamBId(groupWinners.get(1).teamId());

            matchRepository.save(finalMatch);

            markGroupsCompleted(groups);

            return;
        }

        /*
         * 6+ teams.
         *
         * Group winners + best runner-ups
         * until we have 4 qualifiers.
         */
        List<StandingSnapshot> runnerUps = standingsByGroup.values().stream().filter(list -> list.size() > 1).map(list -> list.get(1)).sorted(standingComparator()).toList();

        List<StandingSnapshot> qualifiers = new ArrayList<>(groupWinners);

        int requiredRunnerUps = 4 - qualifiers.size();

        for (int i = 0; i < requiredRunnerUps; i++) {

            qualifiers.add(runnerUps.get(i));
        }

        if (qualifiers.size() != 4) {

            throw new MatchOperationException("Unable to determine exactly 4 qualified teams");
        }

        /*
         * Check cutoff tie.
         */
        if (requiredRunnerUps > 0 && runnerUps.size() > requiredRunnerUps) {

            StandingSnapshot selected = runnerUps.get(requiredRunnerUps - 1);

            StandingSnapshot next = runnerUps.get(requiredRunnerUps);

            if (standingComparator().compare(selected, next) == 0) {

                throw new MatchOperationException("Runner-up qualification could not be resolved automatically");
            }
        }

        Match semifinal1 = createSemifinal(tournamentId, qualifiers.get(0).teamId(), qualifiers.get(1).teamId(), "SF1", 1, 1);

        Match semifinal2 = createSemifinal(tournamentId, qualifiers.get(2).teamId(), qualifiers.get(3).teamId(), "SF2", 1, 2);

        Match finalMatch = createFinal(tournamentId);

        matchRepository.saveAll(List.of(semifinal1, semifinal2, finalMatch));

        markGroupsCompleted(groups);
    }

    private Comparator<StandingSnapshot> standingComparator() {

        return Comparator
                .comparingInt(StandingSnapshot::points)
                .reversed()

                .thenComparing(
                        StandingSnapshot::pointsPerMatch,
                        Comparator.reverseOrder()
                )

                .thenComparing(
                        StandingSnapshot::scoreDifferencePerMatch,
                        Comparator.reverseOrder()
                )

                .thenComparing(
                        StandingSnapshot::normalizedRunRate,
                        Comparator.reverseOrder()
                )

                .thenComparing(
                        StandingSnapshot::winPercentage,
                        Comparator.reverseOrder()
                );
    }

    private void ensureGroupRankingIsResolved(List<StandingSnapshot> standings) {

        if (standings.size() < 2) {
            return;
        }

        StandingSnapshot first = standings.get(0);

        StandingSnapshot second = standings.get(1);

        if (standingComparator().compare(first, second) == 0) {

            throw new MatchOperationException("Group ranking could not be resolved automatically");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<GroupResponse> getTournamentGroups(Long tournamentId) {

        return groupRepository.findAllByTournamentIdOrderBySequenceNumberAsc(tournamentId).stream().map(GroupMapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<StandingResponse> getGroupStandings(Long groupId) {

        Group group = groupRepository.findById(groupId).orElseThrow(() -> new MatchOperationException("Group not found"));

        TournamentRegistrationInfo tournament = tournamentClient.getRegistrationInfo(group.getTournamentId());

        List<StandingSnapshot> standings = calculateGroupStandings(group, tournament);

        Set<Long> qualifiedTeamIds = determineQualifiedTeamIds(group.getTournamentId(), tournament);

        Map<Long, String> teamNames = teamClient.getTournamentTeams(group.getTournamentId()).stream().collect(Collectors.toMap(InternalTeamResponse::id, InternalTeamResponse::name));

        List<StandingResponse> responses = new ArrayList<>();

        for (int i = 0; i < standings.size(); i++) {

            StandingSnapshot standing = standings.get(i);

            responses.add(new StandingResponse(standing.groupId(), standing.teamId(), teamNames.get(standing.teamId()), i + 1, standing.matchesPlayed(), standing.wins(), standing.draws(), standing.losses(), standing.points(), standing.scoreFor(), standing.scoreAgainst(), standing.scoreDifference(), standing.pointsPerMatch(), standing.scoreDifferencePerMatch(), standing.normalizedRunRate(), standing.winPercentage(), qualifiedTeamIds.contains(standing.teamId())));
        }

        return responses;
    }

    private Set<Long> determineQualifiedTeamIds(Long tournamentId, TournamentRegistrationInfo tournament) {

        List<Match> matches = matchRepository.findAllByTournamentIdOrderByRoundNumberAscMatchNumberAsc(tournamentId);

        List<Match> groupMatches = matches.stream().filter(match -> match.getMatchType() == MatchType.GROUP_STAGE).toList();

        if (groupMatches.isEmpty()) {
            return Set.of();
        }

        boolean completed = groupMatches.stream().allMatch(match -> match.getStatus() == MatchStatus.COMPLETED);

        if (!completed) {
            return Set.of();
        }

        List<Group> groups = groupRepository.findAllByTournamentIdOrderBySequenceNumberAsc(tournamentId);

        List<StandingSnapshot> winners = new ArrayList<>();

        List<StandingSnapshot> runnerUps = new ArrayList<>();

        for (Group group : groups) {

            List<StandingSnapshot> standings = calculateGroupStandings(group, tournament);

            if (!standings.isEmpty()) {

                winners.add(standings.get(0));
            }

            if (standings.size() > 1) {

                runnerUps.add(standings.get(1));
            }
        }

        Set<Long> qualified = new LinkedHashSet<>();

        /*
         * 5 teams.
         */
        if (groups.size() == 2) {

            winners.forEach(standing -> qualified.add(standing.teamId()));

            return qualified;
        }

        /*
         * 6+ teams.
         */
        winners.forEach(standing -> qualified.add(standing.teamId()));

        runnerUps.sort(standingComparator());

        while (qualified.size() < 4 && !runnerUps.isEmpty()) {

            qualified.add(runnerUps.remove(0).teamId());
        }

        return qualified;
    }

    @Override
    @Transactional(readOnly = true)
    public PublicTournamentStatsResponse getPublicTournamentStats(String slug) {

        PublicTournamentInfo tournament = tournamentClient.getPublicTournament(slug);

        Long tournamentId = tournament.id();

        List<InternalTeamResponse> teams = teamClient.getTournamentTeams(tournamentId);

        List<Match> matches = matchRepository.findAllByTournamentIdOrderByRoundNumberAscMatchNumberAsc(tournamentId);

        int completedMatches = (int) matches.stream().filter(match -> match.getStatus() == MatchStatus.COMPLETED).count();

        int liveMatches = (int) matches.stream().filter(match -> match.getStatus() == MatchStatus.LIVE).count();

        int upcomingMatches = (int) matches.stream().filter(match -> match.getStatus() == MatchStatus.SCHEDULED || match.getStatus() == MatchStatus.POSTPONED).count();

        List<Group> groups = groupRepository.findAllByTournamentIdOrderBySequenceNumberAsc(tournamentId);

        Set<Long> qualifiedTeamIds = determineQualifiedTeamIds(tournamentId, new TournamentRegistrationInfo(tournament.id(), null, tournament.name(), tournament.visibility(), tournament.status(), tournament.maximumTeams(), null, null, tournament.winPoints(), tournament.drawPoints(), tournament.lossPoints()));

        Map<Long, String> teamNames = teams.stream().collect(Collectors.toMap(InternalTeamResponse::id, InternalTeamResponse::name));

        List<PublicGroupResponse> groupResponses = groups.stream().map(group -> {

            List<StandingResponse> standings = getGroupStandings(group.getId());

            return new PublicGroupResponse(group.getId(), group.getName(), group.getSequenceNumber(), group.getStatus().name(), standings);
        }).toList();

        Match finalMatch = matches.stream().filter(match -> match.getMatchType() == MatchType.FINAL).findFirst().orElse(null);

        Long championId = null;
        String championName = null;

        if (finalMatch != null && finalMatch.getStatus() == MatchStatus.COMPLETED && finalMatch.getWinnerTeamId() != null) {

            championId = finalMatch.getWinnerTeamId();

            championName = teamNames.get(championId);
        }

        String currentStage = determineCurrentStage(matches);

        return new PublicTournamentStatsResponse(tournament.id(), tournament.publicSlug(), tournament.name(), tournament.sportType(), tournament.visibility(), tournament.status(), tournament.format(), teams.size(), matches.size(), completedMatches, liveMatches, upcomingMatches, currentStage, championId, championName, groupResponses);
    }

    private List<StandingSnapshot> calculateGroupStandings(
            Group group,
            TournamentRegistrationInfo tournament
    ) {

        List<GroupTeam> groupTeams =
                groupTeamRepository
                        .findAllByGroupIdOrderBySeedNumberAsc(
                                group.getId()
                        );

        List<Match> matches =
                matchRepository
                        .findAllByGroupIdOrderByMatchNumberAsc(
                                group.getId()
                        );

        List<StandingSnapshot> standings =
                new ArrayList<>();

        for (GroupTeam groupTeam : groupTeams) {

            Long teamId = groupTeam.getTeamId();

            int matchesPlayed = 0;
            int wins = 0;
            int draws = 0;
            int losses = 0;

            int points = 0;

            int scoreFor = 0;
            int scoreAgainst = 0;

            double totalRunRate = 0.0;

            for (Match match : matches) {

                if (match.getStatus() != MatchStatus.COMPLETED) {
                    continue;
                }

                boolean isTeamA =
                        teamId.equals(match.getTeamAId());

                boolean isTeamB =
                        teamId.equals(match.getTeamBId());

                if (!isTeamA && !isTeamB) {
                    continue;
                }

                matchesPlayed++;

                /*
                 * A completed match must always have a winner.
                 */
                if (match.getWinnerTeamId() == null) {

                    throw new MatchOperationException(
                            "Completed match "
                                    + match.getMatchCode()
                                    + " does not have a winner"
                    );
                }

                if (isTeamA) {

                    scoreFor +=
                            safeInt(match.getTeamAScore());

                    scoreAgainst +=
                            safeInt(match.getTeamBScore());

                    totalRunRate +=
                            safeDouble(match.getTeamARunRate());

                } else {

                    scoreFor +=
                            safeInt(match.getTeamBScore());

                    scoreAgainst +=
                            safeInt(match.getTeamAScore());

                    totalRunRate +=
                            safeDouble(match.getTeamBRunRate());
                }

                /*
                 * Winner gets win points.
                 * Loser gets loss points.
                 */
                if (teamId.equals(match.getWinnerTeamId())) {

                    wins++;

                    points +=
                            tournament.winPoints();

                } else {

                    losses++;

                    points +=
                            tournament.lossPoints();
                }
            }

            /*
             * Normalized statistics
             */
            double pointsPerMatch =
                    matchesPlayed == 0
                            ? 0.0
                            : (double) points / matchesPlayed;

            int scoreDifference =
                    scoreFor - scoreAgainst;

            double scoreDifferencePerMatch =
                    matchesPlayed == 0
                            ? 0.0
                            : (double) scoreDifference / matchesPlayed;

            double normalizedRunRate =
                    matchesPlayed == 0
                            ? 0.0
                            : totalRunRate / matchesPlayed;

            double winPercentage =
                    matchesPlayed == 0
                            ? 0.0
                            : (double) wins / matchesPlayed;

            standings.add(
                    new StandingSnapshot(
                            group.getId(),
                            teamId,
                            matchesPlayed,
                            wins,
                            draws,
                            losses,
                            points,
                            scoreFor,
                            scoreAgainst,
                            scoreDifference,
                            pointsPerMatch,
                            scoreDifferencePerMatch,
                            normalizedRunRate,
                            winPercentage
                    )
            );
        }

        /*
         * Ranking:
         *
         * 1. Points
         * 2. Points per match
         * 3. Score difference per match
         * 4. Normalized run rate
         * 5. Win percentage
         */
        standings.sort(standingComparator());

        return standings;
    }
    private Long determineWinner(Match match, CompleteMatchRequest request) {

        Integer teamAScore = request.teamAScore();
        Integer teamBScore = request.teamBScore();

        Double teamARunRate = request.teamARunRate();
        Double teamBRunRate = request.teamBRunRate();

        /*
         * 1. Score decides first.
         */
        if (teamAScore > teamBScore) {
            return match.getTeamAId();
        }

        if (teamBScore > teamAScore) {
            return match.getTeamBId();
        }

        /*
         * 2. Scores are equal -> run rate decides.
         */
        if (Double.compare(teamARunRate, teamBRunRate) > 0) {
            return match.getTeamAId();
        }

        if (Double.compare(teamBRunRate, teamARunRate) > 0) {
            return match.getTeamBId();
        }

        /*
         * 3. Score and run rate are equal.
         * Organizer must decide the winner.
         */
        if (request.winnerTeamId() == null) {
            throw new MatchOperationException("Score and run rate are equal. Organizer must select the winner.");
        }

        /*
         * Organizer can only select Team A or Team B.
         */
        boolean validWinner = request.winnerTeamId().equals(match.getTeamAId()) || request.winnerTeamId().equals(match.getTeamBId());

        if (!validWinner) {
            throw new MatchOperationException("Winner team must be either Team A or Team B.");
        }

        /*
         * Tie-break description is required when organizer decides.
         */
        if (request.tieBreakerDescription() == null || request.tieBreakerDescription().isBlank()) {

            throw new MatchOperationException("Tie-break description is required when score and run rate are equal.");
        }

        return request.winnerTeamId();
    }

    @Override
    public MatchResponse postponeMatch(Long matchId) {

        Match match = findMatch(matchId);

        verifyOrganizer(match.getTournamentId());

        if (match.getStatus() != MatchStatus.SCHEDULED) {

            throw new MatchOperationException("Only a scheduled match can be postponed");
        }

        match.setStatus(MatchStatus.POSTPONED);

        return MatchMapper.toResponse(matchRepository.save(match));
    }

    @Override
    public MatchResponse rescheduleMatch(Long matchId, RescheduleMatchRequest request) {

        Match match = findMatch(matchId);

        verifyOrganizer(match.getTournamentId());

        if (match.getStatus() != MatchStatus.POSTPONED) {

            throw new MatchOperationException("Only a postponed match can be rescheduled");
        }

        match.setScheduledAt(request.scheduledAt());

        match.setStatus(MatchStatus.SCHEDULED);

        return MatchMapper.toResponse(matchRepository.save(match));
    }

    private void updateFinalWithSemifinalWinner(Match semifinal) {

        Match finalMatch = matchRepository.findByTournamentIdAndMatchCode(semifinal.getTournamentId(), "FINAL").orElseThrow(() -> new MatchOperationException("Final match not found"));

        if (semifinal.getMatchCode().equals("SF1")) {

            finalMatch.setTeamAId(semifinal.getWinnerTeamId());
        }

        if (semifinal.getMatchCode().equals("SF2")) {

            finalMatch.setTeamBId(semifinal.getWinnerTeamId());
        }

        matchRepository.save(finalMatch);
    }

    private Match findMatch(Long matchId) {

        return matchRepository.findById(matchId).orElseThrow(() -> new MatchNotFoundException("Match not found with id: " + matchId));
    }

    private void verifyOrganizer(Long tournamentId) {

        Long currentUserId = SecurityUtils.requireCurrentUserId();

        TournamentRegistrationInfo tournament = tournamentClient.getRegistrationInfo(tournamentId);

        if (!tournament.organizerId().equals(currentUserId)) {

            throw new MatchAccessDeniedException("Only the tournament organizer can manage matches");
        }
    }

    private String determineCurrentStage(List<Match> matches) {

        if (matches.isEmpty()) {
            return "NOT_STARTED";
        }

        Match finalMatch = matches.stream().filter(match -> match.getMatchType() == MatchType.FINAL).findFirst().orElse(null);

        if (finalMatch != null) {

            if (finalMatch.getStatus() == MatchStatus.COMPLETED) {

                return "COMPLETED";
            }

            return "FINAL";
        }

        boolean semifinalExists = matches.stream().anyMatch(match -> match.getMatchType() == MatchType.SEMIFINAL);

        if (semifinalExists) {
            return "SEMIFINAL";
        }

        boolean groupExists = matches.stream().anyMatch(match -> match.getMatchType() == MatchType.GROUP_STAGE);

        if (groupExists) {
            return "GROUP_STAGE";
        }

        return "NOT_STARTED";
    }

    private int safeInt(Integer value) {

        return value == null ? 0 : value;
    }

    private double safeDouble(Double value) {

        return value == null ? 0.0 : value;
    }

    private void markGroupsCompleted(List<Group> groups) {

        groups.forEach(group -> group.setStatus(GroupStatus.COMPLETED));

        groupRepository.saveAll(groups);
    }

    @Override
    @Transactional(readOnly = true)
    public PublicMatchResponse getPublicMatch(String slug, String matchCode) {

        PublicTournamentInfo tournament = tournamentClient.getPublicTournament(slug);

        Match match = matchRepository.findByTournamentIdAndMatchCode(tournament.id(), matchCode).orElseThrow(() -> new MatchNotFoundException("Match not found"));

        List<InternalTeamResponse> teams = teamClient.getTournamentTeams(tournament.id());

        Map<Long, String> teamNames = teams.stream().collect(Collectors.toMap(InternalTeamResponse::id, InternalTeamResponse::name));

        String groupName = null;

        if (match.getGroupId() != null) {

            groupName = groupRepository.findById(match.getGroupId()).map(Group::getName).orElse(null);
        }

        String winnerName = null;

        if (match.getWinnerTeamId() != null) {

            winnerName = teamNames.get(match.getWinnerTeamId());
        }

        return new PublicMatchResponse(match.getId(), match.getTournamentId(),

                match.getMatchCode(), match.getMatchType().name(), match.getStatus().name(),

                match.getGroupId(), groupName,

                match.getTeamAId(), teamNames.get(match.getTeamAId()),

                match.getTeamBId(), teamNames.get(match.getTeamBId()),

                match.getWinnerTeamId(), winnerName,

                match.getTeamAScore(), match.getTeamBScore(),

                match.getTeamARunRate(), match.getTeamBRunRate(),

                match.getTieBreakerDescription(),

                match.getScheduledAt(), match.getOriginalScheduledAt(), match.getStartedAt(), match.getCompletedAt());
    }

    @Override
    @Transactional
    public void handleTeamWithdrawal(
            Long tournamentId,
            Long withdrawnTeamId
    ) {

        List<Match> matches =
                matchRepository
                        .findAllByTournamentIdOrderByRoundNumberAscMatchNumberAsc(
                                tournamentId
                        );

        for (Match match : matches) {

            boolean teamA =
                    withdrawnTeamId.equals(
                            match.getTeamAId()
                    );

            boolean teamB =
                    withdrawnTeamId.equals(
                            match.getTeamBId()
                    );

            /*
             * This match does not involve the withdrawn team.
             */
            if (!teamA && !teamB) {
                continue;
            }

            /*
             * Completed matches remain unchanged.
             */
            if (match.getStatus() ==
                    MatchStatus.COMPLETED) {

                continue;
            }

            /*
             * A live match should prevent withdrawal.
             *
             * Ideally this should already be checked before
             * Team Service commits the withdrawal. This check
             * is still useful as a safety guard.
             */
            if (match.getStatus() ==
                    MatchStatus.LIVE) {

                throw new MatchOperationException(
                        "Team cannot be withdrawn while it has a live match: "
                                + match.getMatchCode()
                );
            }

            Long opponentTeamId;

            if (teamA) {
                opponentTeamId =
                        match.getTeamBId();
            } else {
                opponentTeamId =
                        match.getTeamAId();
            }

            /*
             * Safety check.
             */
            if (opponentTeamId == null) {
                continue;
            }

            /*
             * Opponent wins by walkover.
             */
            match.setWinnerTeamId(
                    opponentTeamId
            );

            match.setResultType(
                    MatchResultType.WALKOVER
            );

            match.setStatus(
                    MatchStatus.COMPLETED
            );

            match.setCompletedAt(
                    LocalDateTime.now()
            );

            /*
             * Do not invent a 1-0 / 0-1 score.
             * Leave scores and run rates null.
             */
            match.setTeamAScore(null);
            match.setTeamBScore(null);

            match.setTeamARunRate(null);
            match.setTeamBRunRate(null);

            match.setTieBreakerDescription(
                    "Opponent won by walkover because the opposing team withdrew"
            );

            matchRepository.save(match);
        }

        /*
         * After the walkovers have been applied,
         * continue the normal knockout progression.
         */
        processWithdrawalProgression(
                tournamentId
        );
    }

    private void processWithdrawalProgression(
            Long tournamentId
    ) {

        List<Match> matches =
                matchRepository
                        .findAllByTournamentIdOrderByRoundNumberAscMatchNumberAsc(
                                tournamentId
                        );

        for (Match match : matches) {

            if (match.getStatus() !=
                    MatchStatus.COMPLETED) {
                continue;
            }

            if (match.getMatchType() ==
                    MatchType.SEMIFINAL) {

                updateFinalWithSemifinalWinner(
                        match
                );
            }
        }
    }
}