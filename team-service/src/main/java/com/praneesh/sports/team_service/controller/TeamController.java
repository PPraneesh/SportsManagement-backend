package com.praneesh.sports.team_service.controller;

import com.praneesh.sports.team_service.dto.request.AddTeamMemberRequest;
import com.praneesh.sports.team_service.dto.request.ManualTeamRequest;
import com.praneesh.sports.team_service.dto.request.RegisterTeamRequest;
import com.praneesh.sports.team_service.dto.request.UpdateTeamRequest;

import com.praneesh.sports.team_service.dto.response.TeamMemberResponse;
import com.praneesh.sports.team_service.dto.response.TeamResponse;

import com.praneesh.sports.team_service.service.TeamService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class TeamController {

    private final TeamService teamService;

    public TeamController(
            TeamService teamService
    ) {

        this.teamService = teamService;
    }

    @PostMapping(
            "/api/tournaments/{tournamentId}/teams"
    )
    public ResponseEntity<TeamResponse>
    registerTeam(
            @PathVariable Long tournamentId,
            @Valid @RequestBody RegisterTeamRequest request
    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        teamService.registerTeam(
                                tournamentId,
                                request
                        )
                );
    }

    @PostMapping(
            "/api/tournaments/{tournamentId}/teams/manual"
    )
    public ResponseEntity<TeamResponse>
    manuallyCreateTeam(
            @PathVariable Long tournamentId,
            @Valid @RequestBody ManualTeamRequest request
    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        teamService.manuallyCreateTeam(
                                tournamentId,
                                request
                        )
                );
    }

    @GetMapping(
            "/api/tournaments/{tournamentId}/teams"
    )
    public ResponseEntity<List<TeamResponse>>
    getTournamentTeams(
            @PathVariable Long tournamentId
    ) {

        return ResponseEntity.ok(
                teamService.getTournamentTeams(
                        tournamentId
                )
        );
    }

    @GetMapping(
            "/api/tournaments/{tournamentId}/teams/mine"
    )
    public ResponseEntity<TeamResponse>
    getMyTeam(
            @PathVariable Long tournamentId
    ) {

        return ResponseEntity.ok(
                teamService.getMyTeam(
                        tournamentId
                )
        );
    }

    @GetMapping(
            "/api/tournaments/{tournamentId}/teams/{teamId}"
    )
    public ResponseEntity<TeamResponse>
    getTeamByTournament(
            @PathVariable Long tournamentId,
            @PathVariable Long teamId
    ) {

        /*
         * We don't actually need tournamentId for lookup
         * here, but retaining it makes the API intuitive.
         */
        TeamResponse response =
                teamService.getTeamById(teamId);

        if (!response.tournamentId()
                .equals(tournamentId)) {

            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping(
            "/api/teams/{teamId}"
    )
    public ResponseEntity<TeamResponse>
    getTeamById(
            @PathVariable Long teamId
    ) {

        return ResponseEntity.ok(
                teamService.getTeamById(teamId)
        );
    }

    @PutMapping(
            "/api/teams/{teamId}"
    )
    public ResponseEntity<TeamResponse>
    updateTeam(
            @PathVariable Long teamId,
            @Valid @RequestBody UpdateTeamRequest request
    ) {

        return ResponseEntity.ok(
                teamService.updateTeam(
                        teamId,
                        request
                )
        );
    }

    @GetMapping(
            "/api/teams/{teamId}/members"
    )
    public ResponseEntity<List<TeamMemberResponse>>
    getMembers(
            @PathVariable Long teamId
    ) {

        return ResponseEntity.ok(
                teamService.getMembers(teamId)
        );
    }

    @PostMapping(
            "/api/teams/{teamId}/members"
    )
    public ResponseEntity<TeamMemberResponse>
    addMember(
            @PathVariable Long teamId,
            @Valid @RequestBody AddTeamMemberRequest request
    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        teamService.addMember(
                                teamId,
                                request
                        )
                );
    }

    @DeleteMapping(
            "/api/teams/{teamId}/members/{memberId}"
    )
    public ResponseEntity<Void>
    removeMember(
            @PathVariable Long teamId,
            @PathVariable Long memberId
    ) {

        teamService.removeMember(
                teamId,
                memberId
        );

        return ResponseEntity.noContent().build();
    }

    @PostMapping(
            "/api/teams/{teamId}/withdraw"
    )
    public ResponseEntity<TeamResponse>
    withdrawTeam(
            @PathVariable Long teamId
    ) {

        return ResponseEntity.ok(
                teamService.withdrawTeam(teamId)
        );
    }
}