package com.safecircle.repository;

import com.safecircle.model.CircleMember;
import com.safecircle.model.CircleMemberId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CircleMemberRepository extends JpaRepository<CircleMember, CircleMemberId> {
    List<CircleMember> findByUserId(Long userId);
    List<CircleMember> findByCircleId(Long circleId);
}
