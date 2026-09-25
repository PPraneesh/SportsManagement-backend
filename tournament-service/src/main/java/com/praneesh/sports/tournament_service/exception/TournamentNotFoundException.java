package com.praneesh.sports.tournament_service.exception;


public class TournamentNotFoundException
        extends RuntimeException {

    public TournamentNotFoundException(String message) {
        super(message);
    }
}