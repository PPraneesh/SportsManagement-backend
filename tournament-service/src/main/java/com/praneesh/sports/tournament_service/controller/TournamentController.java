package com.praneesh.sports.tournament_service.controller;


import com.praneesh.sports.tournament_service.dto.request.CreateTournamentRequest;
import com.praneesh.sports.tournament_service.dto.request.UpdateTournamentRequest;
import com.praneesh.sports.tournament_service.dto.response.TournamentResponse;
import com.praneesh.sports.tournament_service.service.TournamentService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tournaments")
public class TournamentController {

    private final TournamentService tournamentService;

    public TournamentController(
            TournamentService tournamentService
    ) {
        this.tournamentService = tournamentService;
    }

    @PostMapping
    public ResponseEntity<TournamentResponse> createTournament(
            @Valid @RequestBody CreateTournamentRequest request
    ) {

        TournamentResponse response =
                tournamentService.createTournament(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/public")
    public ResponseEntity<List<TournamentResponse>>
    getPublicTournaments() {

        return ResponseEntity.ok(
                tournamentService.getPublicTournaments()
        );
    }

    @GetMapping("/public/{slug}")
    public ResponseEntity<TournamentResponse>
    getTournamentBySlug(
            @PathVariable String slug
    ) {

        return ResponseEntity.ok(
                tournamentService.getTournamentBySlug(slug)
        );
    }

    @GetMapping("/my")
    public ResponseEntity<List<TournamentResponse>>
    getMyTournaments() {

        return ResponseEntity.ok(
                tournamentService.getMyTournaments()
        );
    }

    @GetMapping("/{tournamentId}")
    public ResponseEntity<TournamentResponse>
    getTournamentById(
            @PathVariable Long tournamentId
    ) {

        return ResponseEntity.ok(
                tournamentService.getTournamentById(
                        tournamentId
                )
        );
    }

    @PutMapping("/{tournamentId}")
    public ResponseEntity<TournamentResponse>
    updateTournament(
            @PathVariable Long tournamentId,
            @Valid @RequestBody UpdateTournamentRequest request
    ) {

        return ResponseEntity.ok(
                tournamentService.updateTournament(
                        tournamentId,
                        request
                )
        );
    }

    @PostMapping("/{tournamentId}/open-registration")
    public ResponseEntity<TournamentResponse>
    openRegistration(
            @PathVariable Long tournamentId
    ) {

        return ResponseEntity.ok(
                tournamentService.openRegistration(
                        tournamentId
                )
        );
    }

    @PostMapping("/{tournamentId}/close-registration")
    public ResponseEntity<TournamentResponse>
    closeRegistration(
            @PathVariable Long tournamentId
    ) {

        return ResponseEntity.ok(
                tournamentService.closeRegistration(
                        tournamentId
                )
        );
    }

    @PostMapping("/{tournamentId}/cancel")
    public ResponseEntity<TournamentResponse>
    cancelTournament(
            @PathVariable Long tournamentId
    ) {

        return ResponseEntity.ok(
                tournamentService.cancelTournament(
                        tournamentId
                )
        );
    }
}