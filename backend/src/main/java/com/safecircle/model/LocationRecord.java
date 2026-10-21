package com.safecircle.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(
    name = "location_history",
    indexes = {
        @Index(name = "idx_location_user_time", columnList = "user_id, recorded_at DESC")
    }
)
public class LocationRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "location_id")
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Column(name = "accuracy_m", nullable = false)
    private Double accuracyM;

    @Column(name = "recorded_at", nullable = false)
    private Instant recordedAt = Instant.now();

    public LocationRecord() {
    }

    public LocationRecord(User user, Double latitude, Double longitude, Double accuracyM, Instant recordedAt) {
        this.user = user;
        this.latitude = latitude;
        this.longitude = longitude;
        this.accuracyM = accuracyM;
        this.recordedAt = (recordedAt != null) ? recordedAt : Instant.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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
