package com.safecircle.service;

import com.safecircle.dto.CheckinRequest;
import com.safecircle.dto.CheckinResponse;
import com.safecircle.model.CheckinStatus;
import com.safecircle.model.SafetyCheckin;
import com.safecircle.model.User;
import com.safecircle.repository.SafetyCheckinRepository;
import com.safecircle.repository.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SafetyCheckinService {

    private final SafetyCheckinRepository checkinRepository;
    private final UserRepository userRepository;

    public SafetyCheckinService(SafetyCheckinRepository checkinRepository, UserRepository userRepository) {
        this.checkinRepository = checkinRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public CheckinResponse scheduleCheckin(Long userId, CheckinRequest request) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        int minutes = (request.getDueInMinutes() != null && request.getDueInMinutes() > 0)
            ? request.getDueInMinutes()
            : 30;

        Instant dueAt = Instant.now().plus(minutes, ChronoUnit.MINUTES);
        SafetyCheckin checkin = new SafetyCheckin(user, request.getDestinationLabel(), dueAt);

        SafetyCheckin saved = checkinRepository.save(checkin);
        return CheckinResponse.from(saved);
    }

    @Transactional
    public CheckinResponse confirmCheckin(Long userId, Long checkinId) {
        SafetyCheckin checkin = checkinRepository.findById(checkinId)
            .orElseThrow(() -> new IllegalArgumentException("Checkin not found with id: " + checkinId));

        if (!checkin.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("Only the owner can confirm this safety check-in.");
        }

        checkin.setStatus(CheckinStatus.CONFIRMED);
        checkin.setConfirmedAt(Instant.now());

        SafetyCheckin saved = checkinRepository.save(checkin);
        return CheckinResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public List<CheckinResponse> getUserCheckins(Long userId) {
        return checkinRepository.findByUserIdOrderByDueAtDesc(userId)
            .stream()
            .map(CheckinResponse::from)
            .collect(Collectors.toList());
    }
}
