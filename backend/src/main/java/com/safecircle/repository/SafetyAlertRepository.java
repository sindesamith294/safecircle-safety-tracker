package com.safecircle.repository;

import com.safecircle.model.AlertStatus;
import com.safecircle.model.SafetyAlert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SafetyAlertRepository extends JpaRepository<SafetyAlert, Long> {
    List<SafetyAlert> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<SafetyAlert> findByUserIdAndStatus(Long userId, AlertStatus status);
}
