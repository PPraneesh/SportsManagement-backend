package com.praneesh.sports.competition_service.mapper;

import com.praneesh.sports.competition_service.dto.response.GroupResponse;
import com.praneesh.sports.competition_service.entity.Group;
import com.praneesh.sports.competition_service.enums.GroupStatus;

public final class GroupMapper {

    private GroupMapper() {
    }

    public static Group toEntity(
            Long tournamentId,
            String name,
            Integer sequenceNumber
    ) {

        Group group = new Group();

        group.setTournamentId(
                tournamentId
        );

        group.setName(
                name
        );

        group.setSequenceNumber(
                sequenceNumber
        );

        group.setStatus(
                GroupStatus.ACTIVE
        );

        return group;
    }

    public static GroupResponse toResponse(
            Group group
    ) {

        return new GroupResponse(
                group.getId(),
                group.getTournamentId(),
                group.getName(),
                group.getSequenceNumber(),
                group.getStatus().name()
        );
    }
}