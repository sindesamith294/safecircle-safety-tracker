package com.safecircle.dto;

import jakarta.validation.constraints.NotNull;
import java.time.Instant;

public class LocationSubmissionRequest {

    @NotNull(message = "Latitude is required")
    private Double latitude;

    @NotNull(message = "Longitude is required")
    private Double longitude;

    @NotNull(message = "Accuracy is required")
    private Double accuracyM;

    private Instant recordedAt;

    public LocationSubmissionRequest() {
    }

    public LocationSubmissionRequest(Double latitude, Double longitude, Double accuracyM) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.accuracyM = accuracyM;
        this.recordedAt = Instant.now();
    }

    public LocationSubmissionRequest(Double latitude, Double longitude, Double accuracyM, Instant recordedAt) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.accuracyM = accuracyM;
        this.recordedAt = recordedAt;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getAccuracyM() {
        return accuracyM;
    }

    public void setAccuracyM(Double accuracyM) {
        this.accuracyM = accuracyM;
    }

    public Instant getRecordedAt() {
        return recordedAt;
    }

    public void setRecordedAt(Instant recordedAt) {
        this.recordedAt = recordedAt;
    }
}
