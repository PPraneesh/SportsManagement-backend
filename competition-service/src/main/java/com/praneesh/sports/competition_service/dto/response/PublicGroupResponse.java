package com.praneesh.sports.competition_service.dto.response;

import java.util.List;

public record PublicGroupResponse(

        Long groupId,
        String name,
        Integer sequenceNumber,
        String status,

        List<StandingResponse> standings
) {
}