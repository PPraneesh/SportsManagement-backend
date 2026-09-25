package com.praneesh.sports.competition_service.controller;

import com.praneesh.sports.competition_service.dto.response.PublicMatchResponse;
import com.praneesh.sports.competition_service.dto.response.PublicTournamentStatsResponse;
import com.praneesh.sports.competition_service.service.CompetitionService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public/tournaments")
public class PublicCompetitionController {

    private final CompetitionService competitionService;

    public PublicCompetitionController(
            CompetitionService competitionService
    ) {
        this.competitionService =
                competitionService;
    }

    @GetMapping("/{slug}")
    public ResponseEntity<PublicTournamentStatsResponse>
    getTournamentStats(
            @PathVariable String slug
    ) {

        return ResponseEntity.ok(
                competitionService
                        .getPublicTournamentStats(
                                slug
                        )
        );
    }

    @GetMapping("/{slug}/matches/{matchCode}")
    public ResponseEntity<PublicMatchResponse>
    getMatch(
            @PathVariable String slug,
            @PathVariable String matchCode
    ) {

        return ResponseEntity.ok(
                competitionService
                        .getPublicMatch(
                                slug,
                                matchCode
                        )
        );
    }
}