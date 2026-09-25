package com.praneesh.sports.team_service.exception;

public class InvitationNotFoundException
        extends RuntimeException {

    public InvitationNotFoundException(String message) {
        super(message);
    }
}