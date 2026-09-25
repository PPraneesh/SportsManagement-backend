package com.praneesh.sports.competition_service.exception;

public class MatchAccessDeniedException
        extends RuntimeException {

    public MatchAccessDeniedException(String message) {
        super(message);
    }
}