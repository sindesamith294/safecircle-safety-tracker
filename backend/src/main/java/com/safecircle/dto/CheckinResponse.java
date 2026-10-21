package com.safecircle.dto;

import com.safecircle.model.SafetyCheckin;
import java.time.Instant;

public class CheckinResponse {

    private Long checkinId;
    private Long userId;
    private String destinationLabel;
    private Instant dueAt;
    private String status;
    private Instant confirmedAt;
    private Instant createdAt;

    public CheckinResponse() {
    }

    public static CheckinResponse from(SafetyCheckin checkin) {
        if (checkin == null) return null;
        CheckinResponse dto = new CheckinResponse();
        dto.setCheckinId(checkin.getId());
        dto.setUserId(checkin.getUser().getId());
        dto.setDestinationLabel(checkin.getDestinationLabel());
        dto.setDueAt(checkin.getDueAt());
        dto.setStatus(checkin.getStatus() != null ? checkin.getStatus().name() : null);
        dto.setConfirmedAt(checkin.getConfirmedAt());
        dto.setCreatedAt(checkin.getCreatedAt());
        return dto;
    }

    public Long getCheckinId() {
        return checkinId;
    }

    public void setCheckinId(Long checkinId) {
        this.checkinId = checkinId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getDestinationLabel() {
        return destinationLabel;
    }

    public void setDestinationLabel(String destinationLabel) {
        this.destinationLabel = destinationLabel;
    }

    public Instant getDueAt() {
        return dueAt;
    }

    public void setDueAt(Instant dueAt) {
        this.dueAt = dueAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Instant getConfirmedAt() {
        return confirmedAt;
    }

    public void setConfirmedAt(Instant confirmedAt) {
        this.confirmedAt = confirmedAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
