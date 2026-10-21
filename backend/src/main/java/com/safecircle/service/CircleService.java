package com.safecircle.service;

import com.safecircle.dto.CircleCreateRequest;
import com.safecircle.dto.CircleResponse;
import com.safecircle.model.Circle;
import com.safecircle.model.CircleMember;
import com.safecircle.model.User;
import com.safecircle.repository.CircleMemberRepository;
import com.safecircle.repository.CircleRepository;
import com.safecircle.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CircleService {

    private final CircleRepository circleRepository;
    private final CircleMemberRepository circleMemberRepository;
    private final UserRepository userRepository;

    public CircleService(
        CircleRepository circleRepository,
        CircleMemberRepository circleMemberRepository,
        UserRepository userRepository
    ) {
        this.circleRepository = circleRepository;
        this.circleMemberRepository = circleMemberRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public CircleResponse createCircle(Long userId, CircleCreateRequest request) {
        User creator = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        Circle circle = new Circle(request.getName().trim(), creator);
        Circle saved = circleRepository.save(circle);

        CircleMember membership = new CircleMember(saved, creator, "ADMIN");
        circleMemberRepository.save(membership);

        return CircleResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public List<CircleResponse> getUserCircles(Long userId) {
        return circleRepository.findByCreatedById(userId)
            .stream()
            .map(CircleResponse::from)
            .collect(Collectors.toList());
    }
}
