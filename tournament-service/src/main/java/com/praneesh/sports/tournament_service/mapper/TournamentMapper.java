package com.praneesh.sports.tournament_service.mapper;


import com.praneesh.sports.tournament_service.dto.request.CreateTournamentRequest;
import com.praneesh.sports.tournament_service.dto.request.UpdateTournamentRequest;
import com.praneesh.sports.tournament_service.dto.response.TournamentResponse;
import com.praneesh.sports.tournament_service.entity.Tournament;
import com.praneesh.sports.tournament_service.enums.TournamentStatus;

public final class TournamentMapper {

    private TournamentMapper() {
    }

    public static Tournament toEntity(
            CreateTournamentRequest request,
            Long organizerId,
            String publicSlug,
            int defaultWinPoints,
            int defaultDrawPoints,
            int defaultLossPoints
    ) {

        Tournament tournament = new Tournament();

        tournament.setOrganizerId(organizerId);
        tournament.setName(request.name().trim());
        tournament.setDescription(
                request.description() == null
                        ? null
                        : request.description().trim()
        );
        tournament.setSportType(request.sportType().trim());
        tournament.setLocation(request.location().trim());

        tournament.setPublicSlug(publicSlug);

        tournament.setVisibility(request.visibility());

        tournament.setStatus(TournamentStatus.DRAFT);

        /*
         * Format is resolved later based on registered team count.
         */
        tournament.setFormat(null);

        tournament.setMaximumTeams(request.maximumTeams());

        tournament.setWinPoints(
                request.winPoints() == null
                        ? defaultWinPoints
                        : request.winPoints()
        );

        tournament.setDrawPoints(
                request.drawPoints() == null
                        ? defaultDrawPoints
                        : request.drawPoints()
        );

        tournament.setLossPoints(
                request.lossPoints() == null
                        ? defaultLossPoints
                        : request.lossPoints()
        );

        tournament.setRegistrationStart(request.registrationStart());
        tournament.setRegistrationEnd(request.registrationEnd());
        tournament.setStartDate(request.startDate());
        tournament.setEndDate(request.endDate());

        return tournament;
    }

public static void updateEntity(
        Tournament tournament,
        UpdateTournamentRequest request
) {

    tournament.setName(
            request.name()
    );

    tournament.setDescription(
            request.description()
    );

    tournament.setSportType(
            request.sportType()
    );

    tournament.setLocation(
            request.location()
    );

    tournament.setVisibility(
            request.visibility()
    );

    tournament.setMaximumTeams(
            request.maximumTeams()
    );

    tournament.setWinPoints(
            request.winPoints()
    );

    tournament.setDrawPoints(
            request.drawPoints()
    );

    tournament.setLossPoints(
            request.lossPoints()
    );

    tournament.setRegistrationStart(
            request.registrationStart()
    );

    tournament.setRegistrationEnd(
            request.registrationEnd()
    );

    tournament.setStartDate(
            request.startDate()
    );

    tournament.setEndDate(
            request.endDate()
    );
}

    public static TournamentResponse toResponse(
            Tournament tournament
    ) {

        return new TournamentResponse(
                tournament.getId(),
                tournament.getOrganizerId(),
                tournament.getName(),
                tournament.getDescription(),
                tournament.getSportType(),
                tournament.getLocation(),
                tournament.getPublicSlug(),
                tournament.getVisibility(),
                tournament.getStatus(),
                tournament.getFormat(),
                tournament.getMaximumTeams(),
                tournament.getWinPoints(),
                tournament.getDrawPoints(),
                tournament.getLossPoints(),
                tournament.getRegistrationStart(),
                tournament.getRegistrationEnd(),
                tournament.getStartDate(),
                tournament.getEndDate(),
                tournament.getCreatedAt(),
                tournament.getUpdatedAt()
        );
    }
}