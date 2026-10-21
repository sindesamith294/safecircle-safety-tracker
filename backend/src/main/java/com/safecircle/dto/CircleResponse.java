package com.safecircle.dto;

import com.safecircle.model.Circle;
import java.time.Instant;

public class CircleResponse {

    private Long circleId;
    private String name;
    private UserDto createdBy;
    private Instant createdAt;

    public CircleResponse() {
    }

    public static CircleResponse from(Circle circle) {
        if (circle == null) return null;
        CircleResponse dto = new CircleResponse();
        dto.setCircleId(circle.getId());
        dto.setName(circle.getName());
        dto.setCreatedBy(UserDto.from(circle.getCreatedBy()));
        dto.setCreatedAt(circle.getCreatedAt());
        return dto;
    }

    public Long getCircleId() {
        return circleId;
    }

    public void setCircleId(Long circleId) {
        this.circleId = circleId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UserDto getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(UserDto createdBy) {
        this.createdBy = createdBy;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
