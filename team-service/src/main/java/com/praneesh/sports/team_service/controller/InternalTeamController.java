package com.praneesh.sports.team_service.controller;

import com.praneesh.sports.team_service.dto.response.InternalTeamResponse;
import com.praneesh.sports.team_service.service.TeamService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/internal/tournaments")
public class InternalTeamController {

    private final TeamService teamService;

    public InternalTeamController(
            TeamService teamService
    ) {

        this.teamService =
                teamService;
    }

    @GetMapping("/{tournamentId}/teams")
    public ResponseEntity<List<InternalTeamResponse>>
    getTournamentTeams(
            @PathVariable Long tournamentId
    ) {

        return ResponseEntity.ok(
                teamService.getInternalTournamentTeams(
                        tournamentId
                )
        );
    }
}