package com.safecircle.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class CircleMemberId implements Serializable {

    @Column(name = "circle_id")
    private Long circleId;

    @Column(name = "user_id")
    private Long userId;

    public CircleMemberId() {
    }

    public CircleMemberId(Long circleId, Long userId) {
        this.circleId = circleId;
        this.userId = userId;
    }

    public Long getCircleId() {
        return circleId;
    }

    public void setCircleId(Long circleId) {
        this.circleId = circleId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CircleMemberId that = (CircleMemberId) o;
        return Objects.equals(circleId, that.circleId) && Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(circleId, userId);
    }
}
