package com.praneesh.sports.team_service.entity;

import com.praneesh.sports.team_service.enums.TeamMemberRole;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "team_members",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_team_member",
                        columnNames = {
                                "team_id",
                                "user_id"
                        }
                )
        }
)
public class TeamMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /*
     * Reference to Team inside this service.
     */
    @Column(name = "team_id", nullable = false)
    private Long teamId;

    /*
     * Reference to User Service.
     */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "member_role", nullable = false, length = 20)
    private TeamMemberRole memberRole;

    @Column(nullable = false)
    private boolean active;

    @Column(name = "joined_at", nullable = false, updatable = false)
    private LocalDateTime joinedAt;

    public TeamMember() {
    }

    @PrePersist
    protected void onCreate() {
        joinedAt = LocalDateTime.now();
        active = true;
    }

    public Long getId() {
        return id;
    }

    public Long getTeamId() {
        return teamId;
    }

    public void setTeamId(Long teamId) {
        this.teamId = teamId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public TeamMemberRole getMemberRole() {
        return memberRole;
    }

    public void setMemberRole(TeamMemberRole memberRole) {
        this.memberRole = memberRole;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDateTime getJoinedAt() {
        return joinedAt;
    }
}