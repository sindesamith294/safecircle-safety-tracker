package com.safecircle.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "safety_checkins")
public class SafetyCheckin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "checkin_id")
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "destination_label", nullable = false)
    private String destinationLabel;

    @Column(name = "due_at", nullable = false)
    private Instant dueAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CheckinStatus status = CheckinStatus.PENDING;

    @Column(name = "confirmed_at")
    private Instant confirmedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    public SafetyCheckin() {
    }

    public SafetyCheckin(User user, String destinationLabel, Instant dueAt) {
        this.user = user;
        this.destinationLabel = destinationLabel;
        this.dueAt = dueAt;
        this.status = CheckinStatus.PENDING;
        this.createdAt = Instant.now();
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

    public CheckinStatus getStatus() {
        return status;
    }

    public void setStatus(CheckinStatus status) {
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
