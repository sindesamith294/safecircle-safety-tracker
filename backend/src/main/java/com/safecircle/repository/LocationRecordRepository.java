package com.safecircle.repository;

import com.safecircle.model.LocationRecord;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface LocationRecordRepository extends JpaRepository<LocationRecord, Long> {

    Optional<LocationRecord> findFirstByUserIdOrderByRecordedAtDesc(Long userId);

    List<LocationRecord> findTop20ByUserIdOrderByRecordedAtDesc(Long userId);

    List<LocationRecord> findByUserIdAndRecordedAtBetweenOrderByRecordedAtAsc(
        Long userId, Instant start, Instant end
    );

    @Query("SELECT lh FROM LocationRecord lh " +
           "JOIN SharingPermission sp ON sp.owner.id = lh.user.id " +
           "WHERE sp.viewer.id = :viewerId " +
           "  AND sp.owner.id = :ownerId " +
           "  AND sp.status = com.safecircle.model.PermissionStatus.ACTIVE " +
           "  AND (sp.expiresAt IS NULL OR sp.expiresAt > :now) " +
           "ORDER BY lh.recordedAt DESC")
    List<LocationRecord> findAuthorizedLatestLocation(
        @Param("viewerId") Long viewerId,
        @Param("ownerId") Long ownerId,
        @Param("now") Instant now,
        Pageable pageable
    );
}
