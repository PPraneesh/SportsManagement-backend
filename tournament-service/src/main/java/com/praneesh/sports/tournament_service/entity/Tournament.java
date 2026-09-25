package com.praneesh.sports.tournament_service.entity;


import com.praneesh.sports.tournament_service.enums.TournamentFormat;
import com.praneesh.sports.tournament_service.enums.TournamentStatus;
import com.praneesh.sports.tournament_service.enums.TournamentVisibility;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "tournaments",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_tournament_public_slug",
                        columnNames = "public_slug"
                )
        }
)
public class Tournament {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /*
     * This is NOT a database FK to user_db.
     * User Service owns the users table.
     */
    @Column(name = "organizer_id", nullable = false)
    private Long organizerId;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(length = 2000)
    private String description;

    @Column(name = "sport_type", nullable = false, length = 100)
    private String sportType;

    @Column(nullable = false, length = 255)
    private String location;

    @Column(name = "public_slug", nullable = false, unique = true, length = 180)
    private String publicSlug;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TournamentVisibility visibility;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TournamentStatus status;

    /*
     * Null until Competition Service resolves the format
     * after registration closes.
     */
    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private TournamentFormat format;

    @Column(name = "maximum_teams", nullable = false)
    private Integer maximumTeams;

    @Column(name = "win_points", nullable = false)
    private Integer winPoints;

    @Column(name = "draw_points", nullable = false)
    private Integer drawPoints;

    @Column(name = "loss_points", nullable = false)
    private Integer lossPoints;

    @Column(name = "registration_start", nullable = false)
    private LocalDateTime registrationStart;

    @Column(name = "registration_end", nullable = false)
    private LocalDateTime registrationEnd;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public Tournament() {
    }

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Long getOrganizerId() {
        return organizerId;
    }

    public void setOrganizerId(Long organizerId) {
        this.organizerId = organizerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSportType() {
        return sportType;
    }

    public void setSportType(String sportType) {
        this.sportType = sportType;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPublicSlug() {
        return publicSlug;
    }

    public void setPublicSlug(String publicSlug) {
        this.publicSlug = publicSlug;
    }

    public TournamentVisibility getVisibility() {
        return visibility;
    }

    public void setVisibility(TournamentVisibility visibility) {
        this.visibility = visibility;
    }

    public TournamentStatus getStatus() {
        return status;
    }

    public void setStatus(TournamentStatus status) {
        this.status = status;
    }

    public TournamentFormat getFormat() {
        return format;
    }

    public void setFormat(TournamentFormat format) {
        this.format = format;
    }

    public Integer getMaximumTeams() {
        return maximumTeams;
    }

    public void setMaximumTeams(Integer maximumTeams) {
        this.maximumTeams = maximumTeams;
    }

    public Integer getWinPoints() {
        return winPoints;
    }

    public void setWinPoints(Integer winPoints) {
        this.winPoints = winPoints;
    }

    public Integer getDrawPoints() {
        return drawPoints;
    }

    public void setDrawPoints(Integer drawPoints) {
        this.drawPoints = drawPoints;
    }

    public Integer getLossPoints() {
        return lossPoints;
    }

    public void setLossPoints(Integer lossPoints) {
        this.lossPoints = lossPoints;
    }

    public LocalDateTime getRegistrationStart() {
        return registrationStart;
    }

    public void setRegistrationStart(LocalDateTime registrationStart) {
        this.registrationStart = registrationStart;
    }

    public LocalDateTime getRegistrationEnd() {
        return registrationEnd;
    }

    public void setRegistrationEnd(LocalDateTime registrationEnd) {
        this.registrationEnd = registrationEnd;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}