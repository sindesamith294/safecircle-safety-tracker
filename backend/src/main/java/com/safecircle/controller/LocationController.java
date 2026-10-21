package com.safecircle.controller;

import com.safecircle.dto.ApiResponse;
import com.safecircle.dto.LocationResponse;
import com.safecircle.dto.LocationSubmissionRequest;
import com.safecircle.security.UserPrincipal;
import com.safecircle.service.LocationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping
public class LocationController {

    private final LocationService locationService;

    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    /**
     * POST /api/locations
     * Authenticated user's device submits current GPS coordinates, accuracy, and timestamp.
     */
    @PostMapping("/api/locations")
    public ResponseEntity<ApiResponse<LocationResponse>> submitLocation(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody LocationSubmissionRequest request) {
        LocationResponse response = locationService.recordLocation(principal.getId(), request);
        return ResponseEntity.ok(ApiResponse.ok("Location update recorded successfully", response));
    }

    /**
     * GET /api/contacts/{id}/location
     * Retrieve the latest location of contact {id}.
     * Returns location ONLY if the requesting user has an ACTIVE, unexpired sharing permission.
     * Returns 403 Forbidden if permission is missing, rejected, or revoked.
     */
    @GetMapping("/api/contacts/{id}/location")
    public ResponseEntity<ApiResponse<LocationResponse>> getContactLocation(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable("id") Long contactUserId) {
        LocationResponse location = locationService.getAuthorizedContactLocation(principal.getId(), contactUserId);
        return ResponseEntity.ok(ApiResponse.ok("Contact location retrieved", location));
    }

    /**
     * GET /api/locations/latest
     * Retrieve own latest location.
     */
    @GetMapping("/api/locations/latest")
    public ResponseEntity<ApiResponse<LocationResponse>> getOwnLatestLocation(
            @AuthenticationPrincipal UserPrincipal principal) {
        LocationResponse location = locationService.getOwnLatestLocation(principal.getId());
        return ResponseEntity.ok(ApiResponse.ok("Latest location retrieved", location));
    }

    /**
     * GET /api/locations/history
     * Retrieve own location history trail.
     */
    @GetMapping("/api/locations/history")
    public ResponseEntity<ApiResponse<List<LocationResponse>>> getOwnLocationHistory(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(name = "limit", defaultValue = "20") int limit) {
        List<LocationResponse> history = locationService.getOwnLocationHistory(principal.getId(), limit);
        return ResponseEntity.ok(ApiResponse.ok("Location history retrieved", history));
    }
}
