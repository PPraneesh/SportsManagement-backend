package com.praneesh.sports.competition_service.repository;

import com.praneesh.sports.competition_service.entity.GroupTeam;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GroupTeamRepository
        extends JpaRepository<GroupTeam, Long> {

    List<GroupTeam> findAllByGroupIdOrderBySeedNumberAsc(
            Long groupId
    );

    List<GroupTeam> findAllByGroupIdInOrderByGroupIdAscSeedNumberAsc(
            List<Long> groupIds
    );

    boolean existsByGroupIdAndTeamId(
            Long groupId,
            Long teamId
    );
}