package com.praneesh.sports.competition_service.util;

import com.praneesh.sports.competition_service.exception.MatchAccessDeniedException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public final class SecurityUtils {

    private SecurityUtils() {
    }

    public static Optional<Long> getCurrentUserId() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null ||
                !authentication.isAuthenticated()) {

            return Optional.empty();
        }

        Object principal =
                authentication.getPrincipal();

        if (principal instanceof Long userId) {
            return Optional.of(userId);
        }

        return Optional.empty();
    }

    public static Long requireCurrentUserId() {

        return getCurrentUserId()
                .orElseThrow(() ->
                        new MatchAccessDeniedException(
                                "Authentication is required"
                        )
                );
    }
}

