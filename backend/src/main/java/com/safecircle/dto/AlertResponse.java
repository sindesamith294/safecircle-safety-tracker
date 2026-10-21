package com.safecircle.dto;

import com.safecircle.model.SafetyAlert;
import java.time.Instant;

public class AlertResponse {

    private Long alertId;
    private Long userId;
    private String alertType;
    private String severity;
    private String status;
    private Instant createdAt;
    private Instant resolvedAt;
    private String details;

    public AlertResponse() {
    }

    public static AlertResponse from(SafetyAlert alert) {
        if (alert == null) return null;
        AlertResponse dto = new AlertResponse();
        dto.setAlertId(alert.getId());
        dto.setUserId(alert.getUser().getId());
        dto.setAlertType(alert.getAlertType());
        dto.setSeverity(alert.getSeverity() != null ? alert.getSeverity().name() : null);
        dto.setStatus(alert.getStatus() != null ? alert.getStatus().name() : null);
        dto.setCreatedAt(alert.getCreatedAt());
        dto.setResolvedAt(alert.getResolvedAt());
        dto.setDetails(alert.getDetails());
        return dto;
    }

    public Long getAlertId() {
        return alertId;
    }

    public void setAlertId(Long alertId) {
        this.alertId = alertId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getAlertType() {
        return alertType;
    }

    public void setAlertType(String alertType) {
        this.alertType = alertType;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getResolvedAt() {
        return resolvedAt;
    }

    public void setResolvedAt(Instant resolvedAt) {
        this.resolvedAt = resolvedAt;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}
