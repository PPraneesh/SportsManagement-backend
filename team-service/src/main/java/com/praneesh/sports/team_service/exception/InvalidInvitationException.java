package com.praneesh.sports.team_service.exception;

public class InvalidInvitationException
        extends RuntimeException {

    public InvalidInvitationException(String message) {
        super(message);
    }
}