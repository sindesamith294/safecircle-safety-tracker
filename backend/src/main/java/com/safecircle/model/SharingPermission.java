package com.safecircle.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(
    name = "sharing_permissions",
    uniqueConstraints = @UniqueConstraint(name = "uq_owner_viewer", columnNames = {"owner_id", "viewer_id"})
)
public class SharingPermission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "permission_id")
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "viewer_id", nullable = false)
    private User viewer;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PermissionStatus status = PermissionStatus.PENDING;

    @Column(name = "approved_at")
    private Instant approvedAt;

    @Column(name = "revoked_at")
    private Instant revokedAt;

    @Column(name = "expires_at")
    private Instant expiresAt;

    public SharingPermission() {
    }

    public SharingPermission(User owner, User viewer) {
        this.owner = owner;
        this.viewer = viewer;
        this.status = PermissionStatus.PENDING;
    }

    public boolean isActive() {
        if (this.status != PermissionStatus.ACTIVE) {
            return false;
        }
        if (this.expiresAt != null && this.expiresAt.isBefore(Instant.now())) {
            return false;
        }
        return true;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public User getViewer() {
        return viewer;
    }

    public void setViewer(User viewer) {
        this.viewer = viewer;
    }

    public PermissionStatus getStatus() {
        return status;
    }

    public void setStatus(PermissionStatus status) {
        this.status = status;
    }

    public Instant getApprovedAt() {
        return approvedAt;
    }

    public void setApprovedAt(Instant approvedAt) {
        this.approvedAt = approvedAt;
    }

    public Instant getRevokedAt() {
        return revokedAt;
    }

    public void setRevokedAt(Instant revokedAt) {
        this.revokedAt = revokedAt;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }
}
