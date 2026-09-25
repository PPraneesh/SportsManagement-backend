package com.praneesh.sports.user_service.service;


import com.praneesh.sports.user_service.dto.request.RegisterUserRequest;
import com.praneesh.sports.user_service.dto.response.UserResponse;
import com.praneesh.sports.user_service.entity.User;
import com.praneesh.sports.user_service.exception.EmailAlreadyExistsException;
import com.praneesh.sports.user_service.mapper.UserMapper;
import com.praneesh.sports.user_service.repository.UserRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import com.praneesh.sports.user_service.dto.request.LoginRequest;
import com.praneesh.sports.user_service.dto.response.LoginResponse;
import com.praneesh.sports.user_service.exception.InvalidCredentialsException;
import com.praneesh.sports.user_service.security.JwtService;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public UserServiceImpl(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Override
    public UserResponse registerUser(
            RegisterUserRequest request
    ) {

        String email = request.email()
                .trim()
                .toLowerCase(Locale.ROOT);

        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException(
                    "An account already exists with this email"
            );
        }

        String passwordHash =
                passwordEncoder.encode(request.password());

        User user = UserMapper.toEntity(
                request,
                passwordHash
        );

        User savedUser = userRepository.save(user);

        return UserMapper.toResponse(savedUser);
    }

    @Override
    public LoginResponse loginUser(
            LoginRequest request
    ) {

        String email = request.email()
                .trim()
                .toLowerCase(Locale.ROOT);

        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new InvalidCredentialsException(
                                "Invalid email or password"
                        )
                );

        if (!user.isActive()) {
            throw new InvalidCredentialsException(
                    "User account is inactive"
            );
        }

        if (!passwordEncoder.matches(
                request.password(),
                user.getPasswordHash()
        )) {
            throw new InvalidCredentialsException(
                    "Invalid email or password"
            );
        }

        String token = jwtService.generateToken(user);

        return new LoginResponse(
                token,
                "Bearer",
                jwtService.getExpirationMs(),
                UserMapper.toResponse(user)
        );
    }
}