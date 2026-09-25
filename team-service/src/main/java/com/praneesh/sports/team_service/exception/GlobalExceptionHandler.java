package com.praneesh.sports.team_service.exception;

import com.praneesh.sports.team_service.dto.response.ApiErrorResponse;

import org.springframework.dao.DataIntegrityViolationException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.MethodArgumentNotValidException;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(TeamNotFoundException.class)
    public ResponseEntity<ApiErrorResponse>
    handleTeamNotFound(
            TeamNotFoundException ex
    ) {

        return build(
                HttpStatus.NOT_FOUND,
                ex.getMessage()
        );
    }

    @ExceptionHandler(TournamentNotFoundException.class)
    public ResponseEntity<ApiErrorResponse>
    handleTournamentNotFound(
            TournamentNotFoundException ex
    ) {

        return build(
                HttpStatus.NOT_FOUND,
                ex.getMessage()
        );
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiErrorResponse>
    handleUserNotFound(
            UserNotFoundException ex
    ) {

        return build(
                HttpStatus.NOT_FOUND,
                ex.getMessage()
        );
    }

    @ExceptionHandler(InvitationNotFoundException.class)
    public ResponseEntity<ApiErrorResponse>
    handleInvitationNotFound(
            InvitationNotFoundException ex
    ) {

        return build(
                HttpStatus.NOT_FOUND,
                ex.getMessage()
        );
    }

    @ExceptionHandler(TeamAccessDeniedException.class)
    public ResponseEntity<ApiErrorResponse>
    handleAccessDenied(
            TeamAccessDeniedException ex
    ) {

        return build(
                HttpStatus.FORBIDDEN,
                ex.getMessage()
        );
    }

    @ExceptionHandler(TeamOperationException.class)
    public ResponseEntity<ApiErrorResponse>
    handleTeamOperation(
            TeamOperationException ex
    ) {

        return build(
                HttpStatus.CONFLICT,
                ex.getMessage()
        );
    }

    @ExceptionHandler(InternalServiceException.class)
    public ResponseEntity<ApiErrorResponse>
    handleInternalService(
            InternalServiceException ex
    ) {

        return build(
                HttpStatus.SERVICE_UNAVAILABLE,
                ex.getMessage()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse>
    handleValidation(
            MethodArgumentNotValidException ex
    ) {

        String message =
                ex.getBindingResult()
                        .getFieldErrors()
                        .stream()
                        .findFirst()
                        .map(error ->
                                error.getField()
                                        + ": "
                                        + error.getDefaultMessage()
                        )
                        .orElse("Invalid request");

        return build(
                HttpStatus.BAD_REQUEST,
                message
        );
    }

    @ExceptionHandler(
            DataIntegrityViolationException.class
    )
    public ResponseEntity<ApiErrorResponse>
    handleDatabaseConstraint(
            DataIntegrityViolationException ex
    ) {

        return build(
                HttpStatus.CONFLICT,
                "Database constraint violation"
        );
    }

    private ResponseEntity<ApiErrorResponse>
    build(
            HttpStatus status,
            String message
    ) {

        return ResponseEntity
                .status(status)
                .body(
                        new ApiErrorResponse(
                                status.value(),
                                message,
                                LocalDateTime.now()
                        )
                );
    }
}