package com.praneesh.sports.team_service.exception;

public class TeamNotFoundException
        extends RuntimeException {

    public TeamNotFoundException(String message) {
        super(message);
    }
}