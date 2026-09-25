package com.praneesh.sports.competition_service.entity;

import com.praneesh.sports.competition_service.enums.GroupStatus;

import jakarta.persistence.*;

@Entity
@Table(
        name = "tournament_groups",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_group_tournament_sequence",
                        columnNames = {
                                "tournament_id",
                                "sequence_number"
                        }
                )
        }
)
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /*
     * Cross-service ID.
     * No FK to tournament_db.
     */
    @Column(name = "tournament_id", nullable = false)
    private Long tournamentId;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(name = "sequence_number", nullable = false)
    private Integer sequenceNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private GroupStatus status;

    public Group() {
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(Integer sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public GroupStatus getStatus() {
        return status;
    }

    public void setStatus(GroupStatus status) {
        this.status = status;
    }
}