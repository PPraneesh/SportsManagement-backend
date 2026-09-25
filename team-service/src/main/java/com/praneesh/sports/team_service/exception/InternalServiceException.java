package com.praneesh.sports.team_service.exception;

public class InternalServiceException
        extends RuntimeException {

    public InternalServiceException(String message) {
        super(message);
    }
}