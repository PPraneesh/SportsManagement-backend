package com.praneesh.sports.tournament_service.exception;

public class TournamentOperationException
        extends RuntimeException {

    public TournamentOperationException(String message) {
        super(message);
    }
}