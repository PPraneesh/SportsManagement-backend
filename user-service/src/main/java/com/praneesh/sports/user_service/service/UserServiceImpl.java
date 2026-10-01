package com.praneesh.sports.user_service.service;

import com.praneesh.sports.user_service.dto.request.RegisterUserRequest;
import com.praneesh.sports.user_service.dto.response.UserResponse;
import com.praneesh.sports.user_service.entity.User;
import com.praneesh.sports.user_service.exception.EmailAlreadyExistsException;
import com.praneesh.sports.user_service.mapper.UserMapper;
import com.praneesh.sports.user_service.repository.UserRepository;
import com.praneesh.sports.user_service.dto.request.LoginRequest;
import com.praneesh.sports.user_service.dto.response.LoginResponse;
import com.praneesh.sports.user_service.exception.InvalidCredentialsException;
import com.praneesh.sports.user_service.security.JwtService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

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

        log.debug("Verifying email availability for: {}", email);

        if (userRepository.existsByEmail(email)) {
            log.warn("Registration rejected - email already exists: {}", email);
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
        log.info("User registered successfully with id: {} and email: {}", savedUser.getId(), email);

        return UserMapper.toResponse(savedUser);
    }

    @Override
    public LoginResponse loginUser(
            LoginRequest request
    ) {

        String email = request.email()
                .trim()
                .toLowerCase(Locale.ROOT);

        log.debug("Authenticating user: {}", email);

        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("Login failed - user not found: {}", email);
                    return new InvalidCredentialsException(
                            "Invalid email or password"
                    );
                });

        if (!user.isActive()) {
            log.warn("Login failed - account is deactivated: {}", email);
            throw new InvalidCredentialsException(
                    "User account is inactive"
            );
        }

        if (!passwordEncoder.matches(
                request.password(),
                user.getPasswordHash()
        )) {
            log.warn("Login failed - invalid password for user: {}", email);
            throw new InvalidCredentialsException(
                    "Invalid email or password"
            );
        }

        String token = jwtService.generateToken(user);
        log.info("User authenticated successfully, generated JWT for userId: {}", user.getId());

        return new LoginResponse(
                token,
                "Bearer",
                jwtService.getExpirationMs(),
                UserMapper.toResponse(user)
        );
    }
}