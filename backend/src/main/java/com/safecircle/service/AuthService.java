package com.safecircle.service;

import com.safecircle.dto.AuthResponse;
import com.safecircle.dto.LoginRequest;
import com.safecircle.dto.RegisterRequest;
import com.safecircle.dto.UserDto;
import com.safecircle.model.AccountStatus;
import com.safecircle.model.User;
import com.safecircle.repository.UserRepository;
import com.safecircle.security.JwtTokenProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtTokenProvider tokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String cleanPhone = request.getPhoneNumber().trim();
        if (userRepository.existsByPhoneNumber(cleanPhone)) {
            throw new IllegalArgumentException("A user with phone number " + cleanPhone + " is already registered.");
        }

        User user = new User();
        user.setName(request.getName().trim());
        user.setPhoneNumber(cleanPhone);
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setAccountStatus(AccountStatus.ACTIVE);

        User saved = userRepository.save(user);
        String token = tokenProvider.generateToken(saved.getId(), saved.getPhoneNumber(), saved.getName());

        return new AuthResponse(token, saved.getId(), saved.getName(), saved.getPhoneNumber());
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        String cleanPhone = request.getPhoneNumber().trim();
        User user = userRepository.findByPhoneNumber(cleanPhone)
            .orElseThrow(() -> new BadCredentialsException("Invalid phone number or password."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new BadCredentialsException("Invalid phone number or password.");
        }

        String token = tokenProvider.generateToken(user.getId(), user.getPhoneNumber(), user.getName());
        return new AuthResponse(token, user.getId(), user.getName(), user.getPhoneNumber());
    }

    @Transactional(readOnly = true)
    public UserDto getUserProfile(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
        return UserDto.from(user);
    }

    @Transactional(readOnly = true)
    public User getUserEntity(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
    }
}
