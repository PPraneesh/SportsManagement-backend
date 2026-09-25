package com.praneesh.sports.tournament_service.exception;


public class InvalidTournamentException
        extends RuntimeException {

    public InvalidTournamentException(String message) {
        super(message);
    }
}