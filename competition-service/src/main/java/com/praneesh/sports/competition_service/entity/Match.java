package com.praneesh.sports.competition_service.entity;

import com.praneesh.sports.competition_service.enums.MatchStatus;
import com.praneesh.sports.competition_service.enums.MatchType;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "matches",
        indexes = {
                @Index(
                        name = "idx_match_tournament",
                        columnList = "tournament_id"
                )
        }
)
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            name = "tournament_id",
            nullable = false
    )
    private Long tournamentId;

    @Column(name = "group_id")
    private Long groupId;

    @Column(name = "team_a_id")
    private Long teamAId;

    @Column(name = "team_b_id")
    private Long teamBId;

    @Column(name = "winner_team_id")
    private Long winnerTeamId;

    @Column(
            name = "match_code",
            nullable = false,
            length = 50
    )
    private String matchCode;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "match_type",
            nullable = false,
            length = 30
    )
    private MatchType matchType;

    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 20
    )
    private MatchStatus status;

    @Column(
            name = "round_number",
            nullable = false
    )
    private Integer roundNumber;

    @Column(
            name = "match_number",
            nullable = false
    )
    private Integer matchNumber;

    @Column(name = "team_a_score")
    private Integer teamAScore;

    @Column(name = "team_b_score")
    private Integer teamBScore;

    @Column(name = "team_a_run_rate")
    private Double teamARunRate;

    @Column(name = "team_b_run_rate")
    private Double teamBRunRate;

    @Column(
            name = "tie_breaker_description",
            length = 1000
    )
    private String tieBreakerDescription;

    @Column(name = "scheduled_at")
    private LocalDateTime scheduledAt;

    @Column(name = "original_scheduled_at")
    private LocalDateTime originalScheduledAt;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    public Match() {
    }

    public Long getId() {
        return id;
    }

    public Long getTournamentId() {
        return tournamentId;
    }

    public void setTournamentId(Long tournamentId) {
        this.tournamentId = tournamentId;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public Long getTeamAId() {
        return teamAId;
    }

    public void setTeamAId(Long teamAId) {
        this.teamAId = teamAId;
    }

    public Long getTeamBId() {
        return teamBId;
    }

    public void setTeamBId(Long teamBId) {
        this.teamBId = teamBId;
    }

    public Long getWinnerTeamId() {
        return winnerTeamId;
    }

    public void setWinnerTeamId(Long winnerTeamId) {
        this.winnerTeamId = winnerTeamId;
    }

    public String getMatchCode() {
        return matchCode;
    }

    public void setMatchCode(String matchCode) {
        this.matchCode = matchCode;
    }

    public MatchType getMatchType() {
        return matchType;
    }

    public void setMatchType(MatchType matchType) {
        this.matchType = matchType;
    }

    public MatchStatus getStatus() {
        return status;
    }

    public void setStatus(MatchStatus status) {
        this.status = status;
    }

    public Integer getRoundNumber() {
        return roundNumber;
    }

    public void setRoundNumber(Integer roundNumber) {
        this.roundNumber = roundNumber;
    }

    public Integer getMatchNumber() {
        return matchNumber;
    }

    public void setMatchNumber(Integer matchNumber) {
        this.matchNumber = matchNumber;
    }

    public Integer getTeamAScore() {
        return teamAScore;
    }

    public void setTeamAScore(Integer teamAScore) {
        this.teamAScore = teamAScore;
    }

    public Integer getTeamBScore() {
        return teamBScore;
    }

    public void setTeamBScore(Integer teamBScore) {
        this.teamBScore = teamBScore;
    }

    public Double getTeamARunRate() {
        return teamARunRate;
    }

    public void setTeamARunRate(Double teamARunRate) {
        this.teamARunRate = teamARunRate;
    }

    public Double getTeamBRunRate() {
        return teamBRunRate;
    }

    public void setTeamBRunRate(Double teamBRunRate) {
        this.teamBRunRate = teamBRunRate;
    }

    public String getTieBreakerDescription() {
        return tieBreakerDescription;
    }

    public void setTieBreakerDescription(
            String tieBreakerDescription
    ) {
        this.tieBreakerDescription =
                tieBreakerDescription;
    }

    public LocalDateTime getScheduledAt() {
        return scheduledAt;
    }

    public void setScheduledAt(
            LocalDateTime scheduledAt
    ) {
        this.scheduledAt = scheduledAt;
    }

    public LocalDateTime getOriginalScheduledAt() {
        return originalScheduledAt;
    }

    public void setOriginalScheduledAt(
            LocalDateTime originalScheduledAt
    ) {
        this.originalScheduledAt =
                originalScheduledAt;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(
            LocalDateTime startedAt
    ) {
        this.startedAt = startedAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(
            LocalDateTime completedAt
    ) {
        this.completedAt =
                completedAt;
    }
}