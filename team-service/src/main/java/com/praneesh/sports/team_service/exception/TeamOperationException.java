package com.praneesh.sports.team_service.exception;

public class TeamOperationException
        extends RuntimeException {

    public TeamOperationException(String message) {
        super(message);
    }
}