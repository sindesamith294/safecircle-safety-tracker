package com.safecircle.controller;

import com.safecircle.dto.ApiResponse;
import com.safecircle.dto.AuthResponse;
import com.safecircle.dto.LoginRequest;
import com.safecircle.dto.RegisterRequest;
import com.safecircle.dto.UserDto;
import com.safecircle.security.UserPrincipal;
import com.safecircle.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.ok(ApiResponse.ok("User registered successfully", response));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.ok("Login successful", response));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserDto>> getCurrentUser(@AuthenticationPrincipal UserPrincipal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).body(ApiResponse.error("Unauthenticated"));
        }
        UserDto profile = authService.getUserProfile(principal.getId());
        return ResponseEntity.ok(ApiResponse.ok("User profile retrieved", profile));
    }
}
