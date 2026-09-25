package com.praneesh.sports.tournament_service.exception;


public class TournamentAccessDeniedException
        extends RuntimeException {

    public TournamentAccessDeniedException(String message) {
        super(message);
    }
}