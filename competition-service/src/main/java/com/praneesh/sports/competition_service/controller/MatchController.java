package com.praneesh.sports.competition_service.controller;

import com.praneesh.sports.competition_service.dto.request.CompleteMatchRequest;
import com.praneesh.sports.competition_service.dto.request.RescheduleMatchRequest;
import com.praneesh.sports.competition_service.dto.response.MatchResponse;
import com.praneesh.sports.competition_service.service.CompetitionService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class MatchController {

    private final CompetitionService competitionService;

    public MatchController(
            CompetitionService competitionService
    ) {
        this.competitionService =
                competitionService;
    }

    @GetMapping(
            "/tournaments/{tournamentId}/matches"
    )
    public ResponseEntity<List<MatchResponse>>
    getTournamentMatches(
            @PathVariable Long tournamentId
    ) {

        return ResponseEntity.ok(
                competitionService
                        .getTournamentMatches(
                                tournamentId
                        )
        );
    }

    @GetMapping(
            "/matches/{matchId}"
    )
    public ResponseEntity<MatchResponse>
    getMatch(
            @PathVariable Long matchId
    ) {

        return ResponseEntity.ok(
                competitionService
                        .getMatch(matchId)
        );
    }

    @PostMapping(
            "/matches/{matchId}/start"
    )
    public ResponseEntity<MatchResponse>
    startMatch(
            @PathVariable Long matchId
    ) {

        return ResponseEntity.ok(
                competitionService
                        .startMatch(matchId)
        );
    }

    @PostMapping(
            "/matches/{matchId}/complete"
    )
    public ResponseEntity<MatchResponse>
    completeMatch(
            @PathVariable Long matchId,
            @Valid @RequestBody
            CompleteMatchRequest request
    ) {

        return ResponseEntity.ok(
                competitionService
                        .completeMatch(
                                matchId,
                                request
                        )
        );
    }

    @PostMapping(
            "/matches/{matchId}/postpone"
    )
    public ResponseEntity<MatchResponse>
    postponeMatch(
            @PathVariable Long matchId
    ) {

        return ResponseEntity.ok(
                competitionService
                        .postponeMatch(matchId)
        );
    }

    @PostMapping(
            "/matches/{matchId}/reschedule"
    )
    public ResponseEntity<MatchResponse>
    rescheduleMatch(
            @PathVariable Long matchId,
            @Valid @RequestBody
            RescheduleMatchRequest request
    ) {

        return ResponseEntity.ok(
                competitionService
                        .rescheduleMatch(
                                matchId,
                                request
                        )
        );
    }
}