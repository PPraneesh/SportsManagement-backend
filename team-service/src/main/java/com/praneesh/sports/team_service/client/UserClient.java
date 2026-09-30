package com.praneesh.sports.team_service.client;

import com.praneesh.sports.team_service.client.dto.InternalUserResponse;
import com.praneesh.sports.team_service.exception.InternalServiceException;
import com.praneesh.sports.team_service.exception.UserNotFoundException;

import org.springframework.beans.factory.annotation.Qualifier;

import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

public class UserClient {

    private final RestClient restClient;

    public UserClient(
            @Qualifier("userRestClient")
            RestClient restClient
    ) {

        this.restClient = restClient;
    }

    public InternalUserResponse getUser(
            Long id
    ) {

        try {

            return restClient
                    .get()
                    .uri(
                            "/internal/users/{id}",
                            id
                    )
                    .retrieve()
                    .body(
                            InternalUserResponse.class
                    );

        } catch (HttpClientErrorException.NotFound e) {

            throw new UserNotFoundException(
                    "User not found with Id: " + id
            );

        } catch (RestClientException e) {

            throw new InternalServiceException(
                    "Unable to communicate with User Service"
            );
        }
    }

    public InternalUserResponse getUserByEmail(
            String email
    ) {

        try {

            return restClient
                    .get()
                    .uri(
                            uriBuilder ->
                                    uriBuilder
                                            .path("/internal/users/email")
                                            .queryParam("email", email)
                                            .build()
                    )
                    .retrieve()
                    .body(
                            InternalUserResponse.class
                    );

        } catch (HttpClientErrorException.NotFound e) {

            throw new UserNotFoundException(
                    "User not found with email: " + email
            );

        } catch (RestClientException e) {

            throw new InternalServiceException(
                    "Unable to communicate with User Service"
            );
        }
    }

    public InternalUserResponse verifyActiveUserByEmail(
            String email
    ) {

        InternalUserResponse user =
                getUserByEmail(email);

        if (!user.active()) {

            throw new UserNotFoundException(
                    "User account is inactive"
            );
        }
        return user;
    }

    public InternalUserResponse verifyActiveUser(
            Long userId
    ) {

        InternalUserResponse user =
                getUser(userId);

        if (!user.active()) {

            throw new UserNotFoundException(
                    "User account is inactive"
            );
        }
        return user;
    }
}