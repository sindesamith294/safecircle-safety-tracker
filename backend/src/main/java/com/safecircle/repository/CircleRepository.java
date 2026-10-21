package com.safecircle.repository;

import com.safecircle.model.Circle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CircleRepository extends JpaRepository<Circle, Long> {
    List<Circle> findByCreatedById(Long userId);
}
