package com.safecircle.controller;

import com.safecircle.dto.AlertResponse;
import com.safecircle.dto.ApiResponse;
import com.safecircle.model.AlertSeverity;
import com.safecircle.security.UserPrincipal;
import com.safecircle.service.SafetyAlertService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/alerts")
public class AlertController {

    private final SafetyAlertService alertService;

    public AlertController(SafetyAlertService alertService) {
        this.alertService = alertService;
    }

    /**
     * GET /api/alerts
     * View current user's safety alerts.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<AlertResponse>>> getAlerts(
            @AuthenticationPrincipal UserPrincipal principal) {
        List<AlertResponse> alerts = alertService.getUserAlerts(principal.getId());
        return ResponseEntity.ok(ApiResponse.ok("Safety alerts retrieved", alerts));
    }

    /**
     * POST /api/alerts
     * Log a manual or system-generated safety alert.
     */
    @PostMapping
    public ResponseEntity<ApiResponse<AlertResponse>> createAlert(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody Map<String, Object> body) {
        String alertType = (String) body.getOrDefault("alertType", "SAFETY_CHECK");
        String severityStr = (String) body.getOrDefault("severity", "MEDIUM");
        String details = (String) body.getOrDefault("details", "Alert triggered");

        AlertSeverity severity;
        try {
            severity = AlertSeverity.valueOf(severityStr.toUpperCase());
        } catch (Exception e) {
            severity = AlertSeverity.INFO;
        }

        AlertResponse response = alertService.createAlert(principal.getId(), alertType, severity, details);
        return ResponseEntity.ok(ApiResponse.ok("Safety alert generated", response));
    }
}
