package com.praneesh.sports.user_service.controller;

import com.praneesh.sports.user_service.dto.response.InternalUserResponse;
import com.praneesh.sports.user_service.entity.User;
import com.praneesh.sports.user_service.repository.UserRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;

@RestController
@RequestMapping("/internal/users")
public class InternalUserController {

    private static final Logger log = LoggerFactory.getLogger(InternalUserController.class);

    private final UserRepository userRepository;

    public InternalUserController(
            UserRepository userRepository
    ) {

        this.userRepository =
                userRepository;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<InternalUserResponse>
    getUser(
            @PathVariable Long userId
    ) {
        log.debug("Internal lookup request for userId: {}", userId);

        User user =
                userRepository.findById(userId)
                        .orElse(null);

        if (user == null) {
            log.warn("Internal lookup: user with id {} not found", userId);
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(
                new InternalUserResponse(
                        user.getId(),
                        user.getName(),
                        user.getEmail(),
                        user.isActive()
                )
        );
    }

    @GetMapping("/email")
    public ResponseEntity<InternalUserResponse>
    getUserByEmail(
            @RequestParam String email
    ) {
        String normalizedEmail = email.trim().toLowerCase(Locale.ROOT);
        log.debug("Internal lookup request for email: {}", normalizedEmail);

        User user =
                userRepository.findByEmail(normalizedEmail)
                        .orElse(null);

        if (user == null) {
            log.warn("Internal lookup: user with email {} not found", normalizedEmail);
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(
                new InternalUserResponse(
                        user.getId(),
                        user.getName(),
                        user.getEmail(),
                        user.isActive()
                )
        );
    }
}