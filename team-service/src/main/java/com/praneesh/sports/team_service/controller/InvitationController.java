package com.praneesh.sports.team_service.controller;

import com.praneesh.sports.team_service.dto.request.CreateInvitationRequest;
import com.praneesh.sports.team_service.dto.response.TeamInvitationResponse;
import com.praneesh.sports.team_service.service.InvitationService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class InvitationController {

    private final InvitationService invitationService;

    public InvitationController(
            InvitationService invitationService
    ) {
        this.invitationService =
                invitationService;
    }

    @PostMapping(
            "/api/tournaments/{tournamentId}/invitations"
    )
    public ResponseEntity<TeamInvitationResponse>
    createInvitation(
            @PathVariable Long tournamentId,
            @Valid @RequestBody CreateInvitationRequest request
    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        invitationService.createInvitation(
                                tournamentId,
                                request
                        )
                );
    }

    @GetMapping(
            "/api/tournaments/{tournamentId}/invitations"
    )
    public ResponseEntity<List<TeamInvitationResponse>>
    getTournamentInvitations(
            @PathVariable Long tournamentId
    ) {

        return ResponseEntity.ok(
                invitationService
                        .getTournamentInvitations(
                                tournamentId
                        )
        );
    }

    @GetMapping(
            "/api/invitations/my"
    )
    public ResponseEntity<List<TeamInvitationResponse>>
    getMyInvitations() {

        return ResponseEntity.ok(
                invitationService.getMyInvitations()
        );
    }

    @PostMapping(
            "/api/invitations/{token}/accept"
    )
    public ResponseEntity<TeamInvitationResponse>
    acceptInvitation(
            @PathVariable String token
    ) {

        return ResponseEntity.ok(
                invitationService.acceptInvitation(
                        token
                )
        );
    }

    @PostMapping(
            "/api/invitations/{token}/reject"
    )
    public ResponseEntity<TeamInvitationResponse>
    rejectInvitation(
            @PathVariable String token
    ) {

        return ResponseEntity.ok(
                invitationService.rejectInvitation(
                        token
                )
        );
    }
}