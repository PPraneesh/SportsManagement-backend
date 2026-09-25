package com.praneesh.sports.team_service.exception;

public class TeamAccessDeniedException
        extends RuntimeException {

    public TeamAccessDeniedException(String message) {
        super(message);
    }
}