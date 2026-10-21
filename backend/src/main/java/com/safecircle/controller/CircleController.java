package com.safecircle.controller;

import com.safecircle.dto.ApiResponse;
import com.safecircle.dto.CircleCreateRequest;
import com.safecircle.dto.CircleResponse;
import com.safecircle.security.UserPrincipal;
import com.safecircle.service.CircleService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/circles")
public class CircleController {

    private final CircleService circleService;

    public CircleController(CircleService circleService) {
        this.circleService = circleService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CircleResponse>> createCircle(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody CircleCreateRequest request) {
        CircleResponse response = circleService.createCircle(principal.getId(), request);
        return ResponseEntity.ok(ApiResponse.ok("Circle created successfully", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CircleResponse>>> getCircles(
            @AuthenticationPrincipal UserPrincipal principal) {
        List<CircleResponse> circles = circleService.getUserCircles(principal.getId());
        return ResponseEntity.ok(ApiResponse.ok("Circles retrieved", circles));
    }
}
