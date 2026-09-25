package com.praneesh.sports.team_service.entity;

import com.praneesh.sports.team_service.enums.InvitationStatus;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "team_invitations",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_invitation_token",
                        columnNames = "invitation_token"
                )
        }
)
public class TeamInvitation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tournament_id", nullable = false)
    private Long tournamentId;

    @Column(name = "invited_captain_id", nullable = false)
    private Long invitedCaptainId;

    @Column(
            name = "invitation_token",
            nullable = false,
            unique = true,
            length = 150
    )
    private String invitationToken;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private InvitationStatus status;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "responded_at")
    private LocalDateTime respondedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public TeamInvitation() {
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();

        if (status == null) {
            status = InvitationStatus.PENDING;
        }
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

    public Long getInvitedCaptainId() {
        return invitedCaptainId;
    }

    public void setInvitedCaptainId(Long invitedCaptainId) {
        this.invitedCaptainId = invitedCaptainId;
    }

    public String getInvitationToken() {
        return invitationToken;
    }

    public void setInvitationToken(String invitationToken) {
        this.invitationToken = invitationToken;
    }

    public InvitationStatus getStatus() {
        return status;
    }

    public void setStatus(InvitationStatus status) {
        this.status = status;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public LocalDateTime getRespondedAt() {
        return respondedAt;
    }

    public void setRespondedAt(LocalDateTime respondedAt) {
        this.respondedAt = respondedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}