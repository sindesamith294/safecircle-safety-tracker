package com.safecircle.dto;

import com.safecircle.model.LocationRecord;
import java.time.Instant;

public class LocationResponse {

    private Long locationId;
    private Long userId;
    private String userName;
    private Double latitude;
    private Double longitude;
    private Double accuracyM;
    private Instant recordedAt;

    public LocationResponse() {
    }

    public static LocationResponse from(LocationRecord record) {
        if (record == null) return null;
        LocationResponse dto = new LocationResponse();
        dto.setLocationId(record.getId());
        dto.setUserId(record.getUser().getId());
        dto.setUserName(record.getUser().getName());
        dto.setLatitude(record.getLatitude());
        dto.setLongitude(record.getLongitude());
        dto.setAccuracyM(record.getAccuracyM());
        dto.setRecordedAt(record.getRecordedAt());
        return dto;
    }

    public Long getLocationId() {
        return locationId;
    }

    public void setLocationId(Long locationId) {
        this.locationId = locationId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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
