package com.praneesh.sports.tournament_service.controller;

import com.praneesh.sports.tournament_service.dto.response.InternalTournamentRegistrationResponse;
import com.praneesh.sports.tournament_service.service.TournamentService;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal/tournaments")
public class InternalTournamentController {

    private final TournamentService tournamentService;

    public InternalTournamentController(
            TournamentService tournamentService
    ) {

        this.tournamentService =
                tournamentService;
    }

    @GetMapping("/{tournamentId}/registration-info")
    public ResponseEntity<InternalTournamentRegistrationResponse>
    getRegistrationInfo(
            @PathVariable Long tournamentId
    ) {

        return ResponseEntity.ok(
                tournamentService.getRegistrationInfo(
                        tournamentId
                )
        );
    }

    @PostMapping(
            "/{tournamentId}/close-if-capacity-reached"
    )
    public ResponseEntity<Void>
    closeIfCapacityReached(
            @PathVariable Long tournamentId,
            @RequestParam long activeTeamCount
    ) {

        tournamentService.closeRegistrationIfCapacityReached(
                tournamentId,
                activeTeamCount
        );

        return ResponseEntity.noContent().build();
    }
}