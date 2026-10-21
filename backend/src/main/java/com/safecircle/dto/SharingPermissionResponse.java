package com.safecircle.dto;

import com.safecircle.model.SharingPermission;
import java.time.Instant;

public class SharingPermissionResponse {

    private Long permissionId;
    private UserDto owner;
    private UserDto viewer;
    private String status;
    private Instant approvedAt;
    private Instant revokedAt;
    private Instant expiresAt;
    private boolean active;

    public SharingPermissionResponse() {
    }

    public static SharingPermissionResponse from(SharingPermission sp) {
        if (sp == null) return null;
        SharingPermissionResponse dto = new SharingPermissionResponse();
        dto.setPermissionId(sp.getId());
        dto.setOwner(UserDto.from(sp.getOwner()));
        dto.setViewer(UserDto.from(sp.getViewer()));
        dto.setStatus(sp.getStatus() != null ? sp.getStatus().name() : null);
        dto.setApprovedAt(sp.getApprovedAt());
        dto.setRevokedAt(sp.getRevokedAt());
        dto.setExpiresAt(sp.getExpiresAt());
        dto.setActive(sp.isActive());
        return dto;
    }

    public Long getPermissionId() {
        return permissionId;
    }

    public void setPermissionId(Long permissionId) {
        this.permissionId = permissionId;
    }

    public UserDto getOwner() {
        return owner;
    }

    public void setOwner(UserDto owner) {
        this.owner = owner;
    }

    public UserDto getViewer() {
        return viewer;
    }

    public void setViewer(UserDto viewer) {
        this.viewer = viewer;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
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

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
