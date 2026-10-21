package com.safecircle.controller;

import com.safecircle.dto.ApiResponse;
import com.safecircle.dto.CheckinRequest;
import com.safecircle.dto.CheckinResponse;
import com.safecircle.security.UserPrincipal;
import com.safecircle.service.SafetyCheckinService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/checkins")
public class CheckinController {

    private final SafetyCheckinService checkinService;

    public CheckinController(SafetyCheckinService checkinService) {
        this.checkinService = checkinService;
    }

    /**
     * POST /api/checkins
     * Schedule a safety check-in with expected destination and ETA.
     */
    @PostMapping
    public ResponseEntity<ApiResponse<CheckinResponse>> scheduleCheckin(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody CheckinRequest request) {
        CheckinResponse response = checkinService.scheduleCheckin(principal.getId(), request);
        return ResponseEntity.ok(ApiResponse.ok("Safety check-in scheduled successfully", response));
    }

    /**
     * POST /api/checkins/{id}/confirm
     * Confirm safe arrival before or at the due time.
     */
    @PostMapping("/{id}/confirm")
    public ResponseEntity<ApiResponse<CheckinResponse>> confirmCheckin(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable("id") Long checkinId) {
        CheckinResponse response = checkinService.confirmCheckin(principal.getId(), checkinId);
        return ResponseEntity.ok(ApiResponse.ok("Safety check-in confirmed! You are marked safe.", response));
    }

    /**
     * GET /api/checkins
     * List current user's check-ins.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<CheckinResponse>>> getCheckins(
            @AuthenticationPrincipal UserPrincipal principal) {
        List<CheckinResponse> checkins = checkinService.getUserCheckins(principal.getId());
        return ResponseEntity.ok(ApiResponse.ok("Check-ins retrieved", checkins));
    }
}
