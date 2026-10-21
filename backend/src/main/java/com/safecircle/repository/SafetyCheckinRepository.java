package com.safecircle.repository;

import com.safecircle.model.CheckinStatus;
import com.safecircle.model.SafetyCheckin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SafetyCheckinRepository extends JpaRepository<SafetyCheckin, Long> {
    List<SafetyCheckin> findByUserIdOrderByDueAtDesc(Long userId);
    List<SafetyCheckin> findByUserIdAndStatus(Long userId, CheckinStatus status);
}
