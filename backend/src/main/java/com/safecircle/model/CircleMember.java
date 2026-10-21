package com.safecircle.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "circle_members")
public class CircleMember {

    @EmbeddedId
    private CircleMemberId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("circleId")
    @JoinColumn(name = "circle_id")
    private Circle circle;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false, length = 20)
    private String role = "MEMBER";

    @Column(name = "joined_at", nullable = false, updatable = false)
    private Instant joinedAt = Instant.now();

    public CircleMember() {
    }

    public CircleMember(Circle circle, User user, String role) {
        this.id = new CircleMemberId(circle.getId(), user.getId());
        this.circle = circle;
        this.user = user;
        this.role = (role != null) ? role : "MEMBER";
        this.joinedAt = Instant.now();
    }

    public CircleMemberId getId() {
        return id;
    }

    public void setId(CircleMemberId id) {
        this.id = id;
    }

    public Circle getCircle() {
        return circle;
    }

    public void setCircle(Circle circle) {
        this.circle = circle;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Instant getJoinedAt() {
        return joinedAt;
    }

    public void setJoinedAt(Instant joinedAt) {
        this.joinedAt = joinedAt;
    }
}
