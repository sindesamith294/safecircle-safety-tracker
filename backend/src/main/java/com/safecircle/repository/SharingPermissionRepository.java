package com.safecircle.repository;

import com.safecircle.model.PermissionStatus;
import com.safecircle.model.SharingPermission;
import com.safecircle.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface SharingPermissionRepository extends JpaRepository<SharingPermission, Long> {

    Optional<SharingPermission> findByOwnerAndViewer(User owner, User viewer);

    Optional<SharingPermission> findByOwnerIdAndViewerId(Long ownerId, Long viewerId);

    @Query("SELECT sp FROM SharingPermission sp " +
           "WHERE sp.owner.id = :ownerId " +
           "  AND sp.viewer.id = :viewerId " +
           "  AND sp.status = com.safecircle.model.PermissionStatus.ACTIVE " +
           "  AND (sp.expiresAt IS NULL OR sp.expiresAt > :now)")
    Optional<SharingPermission> findActivePermission(
        @Param("ownerId") Long ownerId,
        @Param("viewerId") Long viewerId,
        @Param("now") Instant now
    );

    List<SharingPermission> findByOwnerId(Long ownerId);

    List<SharingPermission> findByOwnerIdAndStatus(Long ownerId, PermissionStatus status);

    List<SharingPermission> findByViewerId(Long viewerId);

    List<SharingPermission> findByViewerIdAndStatus(Long viewerId, PermissionStatus status);
}
