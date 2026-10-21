package com.safecircle.service;

import com.safecircle.dto.SharingPermissionResponse;
import com.safecircle.dto.SharingRequestDto;
import com.safecircle.model.PermissionStatus;
import com.safecircle.model.SharingPermission;
import com.safecircle.model.User;
import com.safecircle.repository.SharingPermissionRepository;
import com.safecircle.repository.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SharingPermissionService {

    private final SharingPermissionRepository permissionRepository;
    private final UserRepository userRepository;

    public SharingPermissionService(SharingPermissionRepository permissionRepository, UserRepository userRepository) {
        this.permissionRepository = permissionRepository;
        this.userRepository = userRepository;
    }

    /**
     * User A (viewer) requests access to User B's (owner) location via User B's phone number.
     * The phone number identifies the user without revealing their location.
     */
    @Transactional
    public SharingPermissionResponse requestSharing(Long viewerId, SharingRequestDto dto) {
        User viewer = userRepository.findById(viewerId)
            .orElseThrow(() -> new IllegalArgumentException("Viewer user not found with id: " + viewerId));

        String cleanPhone = dto.getTargetPhoneNumber().trim();
        User owner = userRepository.findByPhoneNumber(cleanPhone)
            .orElseThrow(() -> new IllegalArgumentException("No registered contact found with phone number: " + cleanPhone));

        if (viewer.getId().equals(owner.getId())) {
            throw new IllegalArgumentException("You cannot request location sharing with yourself.");
        }

        Optional<SharingPermission> existingOpt = permissionRepository.findByOwnerAndViewer(owner, viewer);
        SharingPermission permission;

        if (existingOpt.isPresent()) {
            permission = existingOpt.get();
            // Reset state to PENDING request
            permission.setStatus(PermissionStatus.PENDING);
            permission.setApprovedAt(null);
            permission.setRevokedAt(null);
            if (dto.getExpiresInMinutes() != null && dto.getExpiresInMinutes() > 0) {
                permission.setExpiresAt(Instant.now().plus(dto.getExpiresInMinutes(), ChronoUnit.MINUTES));
            } else {
                permission.setExpiresAt(null);
            }
        } else {
            permission = new SharingPermission(owner, viewer);
            if (dto.getExpiresInMinutes() != null && dto.getExpiresInMinutes() > 0) {
                permission.setExpiresAt(Instant.now().plus(dto.getExpiresInMinutes(), ChronoUnit.MINUTES));
            }
        }

        SharingPermission saved = permissionRepository.save(permission);
        return SharingPermissionResponse.from(saved);
    }

    /**
     * User B (owner) explicitly approves the sharing request.
     */
    @Transactional
    public SharingPermissionResponse approveRequest(Long ownerId, Long permissionId) {
        SharingPermission permission = permissionRepository.findById(permissionId)
            .orElseThrow(() -> new IllegalArgumentException("Sharing request not found with id: " + permissionId));

        if (!permission.getOwner().getId().equals(ownerId)) {
            throw new AccessDeniedException("Only the location owner can approve this sharing request.");
        }

        permission.setStatus(PermissionStatus.ACTIVE);
        permission.setApprovedAt(Instant.now());
        permission.setRevokedAt(null);

        SharingPermission saved = permissionRepository.save(permission);
        return SharingPermissionResponse.from(saved);
    }

    /**
     * User B (owner) rejects the sharing request.
     */
    @Transactional
    public SharingPermissionResponse rejectRequest(Long ownerId, Long permissionId) {
        SharingPermission permission = permissionRepository.findById(permissionId)
            .orElseThrow(() -> new IllegalArgumentException("Sharing request not found with id: " + permissionId));

        if (!permission.getOwner().getId().equals(ownerId)) {
            throw new AccessDeniedException("Only the location owner can reject this sharing request.");
        }

        permission.setStatus(PermissionStatus.REJECTED);
        SharingPermission saved = permissionRepository.save(permission);
        return SharingPermissionResponse.from(saved);
    }

    /**
     * User B (owner) or User A (viewer) revokes sharing.
     * When revoked, all subsequent location queries by viewer are immediately denied.
     */
    @Transactional
    public SharingPermissionResponse revokePermission(Long userId, Long permissionId) {
        SharingPermission permission = permissionRepository.findById(permissionId)
            .orElseThrow(() -> new IllegalArgumentException("Sharing permission not found with id: " + permissionId));

        boolean isOwner = permission.getOwner().getId().equals(userId);
        boolean isViewer = permission.getViewer().getId().equals(userId);

        if (!isOwner && !isViewer) {
            throw new AccessDeniedException("You are not authorized to revoke this permission.");
        }

        permission.setStatus(PermissionStatus.REVOKED);
        permission.setRevokedAt(Instant.now());

        SharingPermission saved = permissionRepository.save(permission);
        return SharingPermissionResponse.from(saved);
    }

    /**
     * Checks if viewer has an active, unexpired consent record for owner.
     */
    @Transactional(readOnly = true)
    public boolean hasActivePermission(Long viewerId, Long ownerId) {
        return permissionRepository.findActivePermission(ownerId, viewerId, Instant.now()).isPresent();
    }

    @Transactional(readOnly = true)
    public List<SharingPermissionResponse> getIncomingPendingRequests(Long ownerId) {
        return permissionRepository.findByOwnerIdAndStatus(ownerId, PermissionStatus.PENDING)
            .stream()
            .map(SharingPermissionResponse::from)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SharingPermissionResponse> getActiveViewers(Long ownerId) {
        return permissionRepository.findByOwnerIdAndStatus(ownerId, PermissionStatus.ACTIVE)
            .stream()
            .filter(SharingPermission::isActive)
            .map(SharingPermissionResponse::from)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SharingPermissionResponse> getApprovedContactsToView(Long viewerId) {
        return permissionRepository.findByViewerIdAndStatus(viewerId, PermissionStatus.ACTIVE)
            .stream()
            .filter(SharingPermission::isActive)
            .map(SharingPermissionResponse::from)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SharingPermissionResponse> getAllPermissionsForUser(Long userId) {
        List<SharingPermission> asOwner = permissionRepository.findByOwnerId(userId);
        List<SharingPermission> asViewer = permissionRepository.findByViewerId(userId);

        return java.util.stream.Stream.concat(asOwner.stream(), asViewer.stream())
            .distinct()
            .map(SharingPermissionResponse::from)
            .collect(Collectors.toList());
    }
}
