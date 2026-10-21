package com.safecircle.service;

import com.safecircle.dto.AlertResponse;
import com.safecircle.model.AlertSeverity;
import com.safecircle.model.AlertStatus;
import com.safecircle.model.SafetyAlert;
import com.safecircle.model.User;
import com.safecircle.repository.SafetyAlertRepository;
import com.safecircle.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SafetyAlertService {

    private final SafetyAlertRepository alertRepository;
    private final UserRepository userRepository;

    public SafetyAlertService(SafetyAlertRepository alertRepository, UserRepository userRepository) {
        this.alertRepository = alertRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public AlertResponse createAlert(Long userId, String alertType, AlertSeverity severity, String details) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        SafetyAlert alert = new SafetyAlert(
            user,
            alertType != null ? alertType : "UNUSUAL_MOVEMENT",
            severity != null ? severity : AlertSeverity.INFO,
            details
        );

        SafetyAlert saved = alertRepository.save(alert);
        return AlertResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public List<AlertResponse> getUserAlerts(Long userId) {
        return alertRepository.findByUserIdOrderByCreatedAtDesc(userId)
            .stream()
            .map(AlertResponse::from)
            .collect(Collectors.toList());
    }
}
