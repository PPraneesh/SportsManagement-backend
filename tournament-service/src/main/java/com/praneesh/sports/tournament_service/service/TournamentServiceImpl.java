package com.praneesh.sports.tournament_service.service;


import com.praneesh.sports.tournament_service.dto.request.CreateTournamentRequest;
import com.praneesh.sports.tournament_service.dto.request.UpdateTournamentRequest;
import com.praneesh.sports.tournament_service.dto.response.InternalTournamentRegistrationResponse;
import com.praneesh.sports.tournament_service.dto.response.TournamentResponse;
import com.praneesh.sports.tournament_service.entity.Tournament;
import com.praneesh.sports.tournament_service.enums.TournamentStatus;
import com.praneesh.sports.tournament_service.enums.TournamentVisibility;
import com.praneesh.sports.tournament_service.exception.TournamentAccessDeniedException;
import com.praneesh.sports.tournament_service.exception.TournamentNotFoundException;
import com.praneesh.sports.tournament_service.exception.TournamentOperationException;
import com.praneesh.sports.tournament_service.mapper.TournamentMapper;
import com.praneesh.sports.tournament_service.repository.TournamentRepository;
import com.praneesh.sports.tournament_service.util.SecurityUtils;
import com.praneesh.sports.tournament_service.util.SlugUtil;
import com.praneesh.sports.tournament_service.util.TournamentValidationUtil;
import org.springframework.context.ApplicationEventPublisher;

import java.util.UUID;

import com.praneesh.sports.tournament_service.dto.event.TournamentRegistrationClosedEvent;
import com.praneesh.sports.tournament_service.enums.TournamentEventType;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class TournamentServiceImpl
        implements TournamentService {

    private static final int DEFAULT_WIN_POINTS = 3;
    private static final int DEFAULT_DRAW_POINTS = 1;
    private static final int DEFAULT_LOSS_POINTS = 0;

    private final TournamentRepository tournamentRepository;

    private final ApplicationEventPublisher applicationEventPublisher;

    public TournamentServiceImpl(
            TournamentRepository tournamentRepository,
            ApplicationEventPublisher applicationEventPublisher
    ) {

        this.tournamentRepository =
                tournamentRepository;

        this.applicationEventPublisher =
                applicationEventPublisher;
    }
    @Override
    public TournamentResponse createTournament(
            CreateTournamentRequest request
    ) {

        Long organizerId =
                SecurityUtils.requireCurrentUserId();

        TournamentValidationUtil.validateDates(
                request.registrationStart(),
                request.registrationEnd(),
                request.startDate(),
                request.endDate()
        );

        TournamentValidationUtil.validatePoints(
                request.winPoints(),
                request.drawPoints(),
                request.lossPoints()
        );

        String slug =
                generateUniqueSlug(request.name());

        Tournament tournament =
                TournamentMapper.toEntity(
                        request,
                        organizerId,
                        slug,
                        DEFAULT_WIN_POINTS,
                        DEFAULT_DRAW_POINTS,
                        DEFAULT_LOSS_POINTS
                );

        Tournament saved =
                tournamentRepository.save(tournament);

        return TournamentMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public TournamentResponse getTournamentById(
            Long tournamentId
    ) {

        Tournament tournament =
                findTournament(tournamentId);

        return TournamentMapper.toResponse(tournament);
    }

    @Override
    @Transactional(readOnly = true)
    public TournamentResponse getTournamentBySlug(
            String slug
    ) {

        Tournament tournament =
                tournamentRepository.findByPublicSlug(slug)
                        .orElseThrow(() ->
                                new TournamentNotFoundException(
                                        "Tournament not found"
                                )
                        );

        return TournamentMapper.toResponse(tournament);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TournamentResponse> getPublicTournaments() {

        List<Tournament> tournaments =
                tournamentRepository.findActivePublicTournaments(
                        TournamentVisibility.PUBLIC,
                        TournamentStatus.OPEN,
                        LocalDateTime.now()
                );

        return tournaments.stream()
                .map(TournamentMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TournamentResponse> getMyTournaments() {

        Long organizerId =
                SecurityUtils.requireCurrentUserId();

        return tournamentRepository
                .findByOrganizerIdOrderByCreatedAtDesc(organizerId)
                .stream()
                .map(TournamentMapper::toResponse)
                .toList();
    }

@Override
public TournamentResponse updateTournament(
        Long tournamentId,
        UpdateTournamentRequest request
) {

    Tournament tournament =
            findTournament(tournamentId);

    verifyOrganizer(tournament);

    if (tournament.getStatus() != TournamentStatus.DRAFT &&
            tournament.getStatus() != TournamentStatus.OPEN) {

        throw new TournamentOperationException(
                "Tournament cannot be edited after registration closes"
        );
    }

    if (tournament.getStatus() == TournamentStatus.OPEN &&
            !request.maximumTeams()
                    .equals(tournament.getMaximumTeams())) {

        throw new TournamentOperationException(
                "Maximum teams cannot be changed after registration opens"
        );
    }


    TournamentValidationUtil.validateUpdateDates(
            tournament,
            request.registrationStart(),
            request.registrationEnd(),
            request.startDate(),
            request.endDate()
    );


    TournamentValidationUtil.validatePoints(
            request.winPoints(),
            request.drawPoints(),
            request.lossPoints()
    );


    TournamentMapper.updateEntity(
            tournament,
            request
    );


    Tournament saved =
            tournamentRepository.save(tournament);

    return TournamentMapper.toResponse(saved);
}

    @Override
    public TournamentResponse openRegistration(
            Long tournamentId
    ) {

        Tournament tournament =
                findTournament(tournamentId);

        verifyOrganizer(tournament);

        if (tournament.getStatus() != TournamentStatus.DRAFT) {

            throw new TournamentOperationException(
                    "Only a draft tournament can be opened"
            );
        }

        if (tournament.getRegistrationEnd()
                .isBefore(LocalDateTime.now())) {

            throw new TournamentOperationException(
                    "Registration end date has already passed"
            );
        }

        tournament.setStatus(
                TournamentStatus.OPEN
        );

        return TournamentMapper.toResponse(
                tournamentRepository.save(tournament)
        );
    }

    @Override
    public TournamentResponse closeRegistration(
            Long tournamentId
    ) {

        Tournament tournament =
                findTournament(tournamentId);

        verifyOrganizer(tournament);

        if (tournament.getStatus() != TournamentStatus.OPEN) {

            throw new TournamentOperationException(
                    "Only an open tournament can close registration"
            );
        }

        tournament.setStatus(
                TournamentStatus.REGISTRATION_CLOSED
        );

        Tournament saved =
                tournamentRepository.save(tournament);

        applicationEventPublisher.publishEvent(
                new TournamentRegistrationClosedEvent(
                        TournamentEventType.TOURNAMENT_REGISTRATION_CLOSED,
                        UUID.randomUUID(),
                        tournament.getId(),
                        LocalDateTime.now()
                )
        );

        return TournamentMapper.toResponse(saved);
    }
    @Override
    public TournamentResponse cancelTournament(
            Long tournamentId
    ) {

        Tournament tournament =
                findTournament(tournamentId);

        verifyOrganizer(tournament);

        if (tournament.getStatus() ==
                TournamentStatus.COMPLETED) {

            throw new TournamentOperationException(
                    "A completed tournament cannot be cancelled"
            );
        }

        if (tournament.getStatus() ==
                TournamentStatus.CANCELLED) {

            throw new TournamentOperationException(
                    "Tournament is already cancelled"
            );
        }

        tournament.setStatus(
                TournamentStatus.CANCELLED
        );

        return TournamentMapper.toResponse(
                tournamentRepository.save(tournament)
        );
    }

    private Tournament findTournament(
            Long tournamentId
    ) {

        return tournamentRepository.findById(tournamentId)
                .orElseThrow(() ->
                        new TournamentNotFoundException(
                                "Tournament not found with id: "
                                        + tournamentId
                        )
                );
    }

    private void verifyOrganizer(
            Tournament tournament
    ) {

        Long currentUserId =
                SecurityUtils.requireCurrentUserId();

        if (!currentUserId.equals(
                tournament.getOrganizerId()
        )) {

            throw new TournamentAccessDeniedException(
                    "You are not the organizer of this tournament"
            );
        }
    }

    private String generateUniqueSlug(
            String tournamentName
    ) {

        String baseSlug =
                SlugUtil.toSlug(tournamentName);

        String slug = baseSlug;

        int counter = 2;

        while (tournamentRepository.existsByPublicSlug(slug)) {

            slug = baseSlug + "-" + counter;
            counter++;
        }

        return slug;
    }

    @Override
    @Transactional(readOnly = true)
    public InternalTournamentRegistrationResponse
    getRegistrationInfo(Long tournamentId) {

        Tournament tournament =
                findTournament(tournamentId);

        return new InternalTournamentRegistrationResponse(
                tournament.getId(),
                tournament.getOrganizerId(),
                tournament.getName(),
                tournament.getVisibility().name(),
                tournament.getStatus().name(),
                tournament.getMaximumTeams(),
                tournament.getRegistrationStart(),
                tournament.getRegistrationEnd(),

                tournament.getWinPoints(),
                tournament.getDrawPoints(),
                tournament.getLossPoints()
        );
    }

    @Override
    public void closeRegistrationIfCapacityReached(
            Long tournamentId,
            long activeTeamCount
    ) {

        Tournament tournament =
                findTournament(tournamentId);

        if (tournament.getStatus() ==
                TournamentStatus.OPEN &&
                activeTeamCount >=
                        tournament.getMaximumTeams()) {

            tournament.setStatus(
                    TournamentStatus.REGISTRATION_CLOSED
            );

            tournamentRepository.save(tournament);

            applicationEventPublisher.publishEvent(
                    new TournamentRegistrationClosedEvent(
                            TournamentEventType.TOURNAMENT_REGISTRATION_CLOSED,
                            UUID.randomUUID(),
                            tournament.getId(),
                            LocalDateTime.now()
                    )
            );
        }
    }

    @Override
    public void completeTournament(
            Long tournamentId
    ) {

        Tournament tournament =
                findTournament(tournamentId);

        /*
         * Idempotency:
         * if Kafka redelivers the event,
         * don't fail the operation.
         */
        if (tournament.getStatus() ==
                TournamentStatus.COMPLETED) {

            return;
        }

        /*
         * A cancelled tournament must not
         * become completed.
         */
        if (tournament.getStatus() ==
                TournamentStatus.CANCELLED) {

            return;
        }

        tournament.setStatus(
                TournamentStatus.COMPLETED
        );

        tournamentRepository.save(tournament);
    }

}