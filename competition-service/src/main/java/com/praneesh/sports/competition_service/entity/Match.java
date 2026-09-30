package com.praneesh.sports.competition_service.entity;

import com.praneesh.sports.competition_service.enums.MatchResultType;
import com.praneesh.sports.competition_service.enums.MatchStatus;
import com.praneesh.sports.competition_service.enums.MatchType;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
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

    @Enumerated(EnumType.STRING)
    @Column(name = "result_type", length = 20)
    private MatchResultType resultType;

    @Column(name = "scheduled_at")
    private LocalDateTime scheduledAt;

    @Column(name = "original_scheduled_at")
    private LocalDateTime originalScheduledAt;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

}