package com.praneesh.sports.competition_service.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(
        name = "group_teams",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_group_team",
                        columnNames = {
                                "group_id",
                                "team_id"
                        }
                )
        }
)
public class GroupTeam {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /*
     * Local Group ID.
     */
    @Column(name = "group_id", nullable = false)
    private Long groupId;

    /*
     * Cross-service Team ID.
     */
    @Column(name = "team_id", nullable = false)
    private Long teamId;

    @Column(name = "seed_number", nullable = false)
    private Integer seedNumber;

    public GroupTeam() {
    }
}