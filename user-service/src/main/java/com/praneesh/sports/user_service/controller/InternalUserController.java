package com.praneesh.sports.user_service.controller;

import com.praneesh.sports.user_service.dto.response.InternalUserResponse;
import com.praneesh.sports.user_service.entity.User;
import com.praneesh.sports.user_service.repository.UserRepository;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.Locale;

@RestController
@RequestMapping("/internal/users")
public class InternalUserController {

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

        User user =
                userRepository.findById(userId)
                        .orElse(null);

        if (user == null) {
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

        User user =
                userRepository.findByEmail(email.trim().toLowerCase(Locale.ROOT))
                        .orElse(null);

        if (user == null) {
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