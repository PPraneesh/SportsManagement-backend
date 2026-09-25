package com.praneesh.sports.competition_service.exception;

public class MatchNotFoundException
        extends RuntimeException {

    public MatchNotFoundException(String message) {
        super(message);
    }
}