package com.safecircle.service;

import com.safecircle.dto.LocationResponse;
import com.safecircle.dto.LocationSubmissionRequest;
import com.safecircle.model.LocationRecord;
import com.safecircle.model.User;
import com.safecircle.repository.LocationRecordRepository;
import com.safecircle.repository.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LocationService {

    private final LocationRecordRepository locationRecordRepository;
    private final UserRepository userRepository;
    private final SharingPermissionService sharingPermissionService;

    public LocationService(
        LocationRecordRepository locationRecordRepository,
        UserRepository userRepository,
        SharingPermissionService sharingPermissionService
    ) {
        this.locationRecordRepository = locationRecordRepository;
        this.userRepository = userRepository;
        this.sharingPermissionService = sharingPermissionService;
    }

    /**
     * Submit a user's device GPS update.
     */
    @Transactional
    public LocationResponse recordLocation(Long userId, LocationSubmissionRequest request) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        Instant recordedAt = request.getRecordedAt() != null ? request.getRecordedAt() : Instant.now();
        LocationRecord record = new LocationRecord(
            user,
            request.getLatitude(),
            request.getLongitude(),
            request.getAccuracyM(),
            recordedAt
        );

        LocationRecord saved = locationRecordRepository.save(record);
        return LocationResponse.from(saved);
    }

    /**
     * Retrieve the latest location of a user ONLY when the requesting viewer has active permission.
     * Enforces the architectural rule: "The backend must check permissions on every read and subscription...
     * When sharing is revoked, access to subsequent updates stops immediately."
     */
    @Transactional(readOnly = true)
    public LocationResponse getAuthorizedContactLocation(Long viewerId, Long ownerId) {
        // Enforce explicit consent check
        if (!sharingPermissionService.hasActivePermission(viewerId, ownerId)) {
            throw new AccessDeniedException(
                "Access denied: You do not have active sharing permission to view contact #" + ownerId + "'s location."
            );
        }

        LocationRecord latest = locationRecordRepository.findFirstByUserIdOrderByRecordedAtDesc(ownerId)
            .orElseThrow(() -> new IllegalArgumentException("No location data available yet for contact #" + ownerId));

        return LocationResponse.from(latest);
    }

    @Transactional(readOnly = true)
    public List<LocationResponse> getOwnLocationHistory(Long userId, int limit) {
        return locationRecordRepository.findTop20ByUserIdOrderByRecordedAtDesc(userId)
            .stream()
            .map(LocationResponse::from)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LocationResponse getOwnLatestLocation(Long userId) {
        return locationRecordRepository.findFirstByUserIdOrderByRecordedAtDesc(userId)
            .map(LocationResponse::from)
            .orElse(null);
    }
}
