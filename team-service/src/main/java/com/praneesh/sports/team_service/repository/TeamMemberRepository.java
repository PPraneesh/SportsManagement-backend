package com.praneesh.sports.team_service.repository;

import com.praneesh.sports.team_service.entity.TeamMember;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TeamMemberRepository
        extends JpaRepository<TeamMember, Long> {

    List<TeamMember> findAllByTeamIdAndActiveTrueOrderByJoinedAtAsc(
            Long teamId
    );

    Optional<TeamMember> findByIdAndTeamId(
            Long memberId,
            Long teamId
    );

    boolean existsByTeamIdAndUserIdAndActiveTrue(
            Long teamId,
            Long userId
    );

    boolean existsByTeamIdInAndUserIdAndActiveTrue(
            List<Long> teamIds,
            Long userId
    );

    List<TeamMember> findAllByUserIdAndActiveTrueOrderByJoinedAtDesc(
            Long userId
    );
}