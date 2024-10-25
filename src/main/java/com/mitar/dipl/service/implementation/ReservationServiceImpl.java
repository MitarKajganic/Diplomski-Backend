package com.mitar.dipl.service.implementation;

import com.mitar.dipl.exception.custom.BadRequestException;
import com.mitar.dipl.exception.custom.ConflictException;
import com.mitar.dipl.exception.custom.ResourceNotFoundException;
import com.mitar.dipl.mapper.ReservationMapper;
import com.mitar.dipl.model.dto.reservation.ReservationCreateDto;
import com.mitar.dipl.model.dto.reservation.ReservationDto;
import com.mitar.dipl.model.entity.Reservation;
import com.mitar.dipl.model.entity.TableEntity;
import com.mitar.dipl.model.entity.User;
import com.mitar.dipl.repository.ReservationRepository;
import com.mitar.dipl.repository.TableRepository;
import com.mitar.dipl.repository.UserRepository;
import com.mitar.dipl.service.ReservationService;
import com.mitar.dipl.utils.UUIDUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Service
@AllArgsConstructor
@Slf4j
@Transactional
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final TableRepository tableRepository;
    private final ReservationMapper reservationMapper;

    @PersistenceContext
    private EntityManager entityManager;

    private static final LocalTime OPENING_TIME = LocalTime.of(10, 0);
    private static final LocalTime CLOSING_TIME = LocalTime.of(22, 0);
    private static final Duration BUFFER_DURATION = Duration.ofMinutes(30);
    private static final Duration RESERVATION_DURATION = Duration.ofHours(2);


    @Override
    public List<ReservationDto> getAllReservations() {
        log.info("Fetching all reservations.");
        List<ReservationDto> reservationDtos = reservationRepository.findAll().stream()
                .map(reservationMapper::toDto)
                .toList();
        log.info("Fetched {} reservations.", reservationDtos.size());
        return reservationDtos;
    }

    @Override
    public List<ReservationDto> getAllIncludingDeleted() {
        log.info("Fetching all reservations including deleted.");
        List<ReservationDto> reservationDtos = reservationRepository.findAllIncludingDeleted().stream()
                .map(reservationMapper::toDto)
                .toList();
        log.info("Fetched {} reservations including deleted.", reservationDtos.size());
        return reservationDtos;
    }

    @Override
    public ReservationDto getReservationById(String reservationId) {
        UUID reservationUUID = UUIDUtils.parseUUID(reservationId);
        log.debug("Fetching Reservation with ID: {}", reservationUUID);

        Reservation reservation = reservationRepository.findById(reservationUUID)
                .orElseThrow(() -> {
                    log.warn("Reservation not found with ID: {}", reservationId);
                    return new ResourceNotFoundException("Reservation not found with ID: " + reservationId);
                });

        ReservationDto reservationDto = reservationMapper.toDto(reservation);
        log.info("Retrieved Reservation ID: {}", reservationId);
        return reservationDto;
    }

    @Override
    public List<ReservationDto> getReservationsByUserId(String userId) {
        UUID userUUID = UUIDUtils.parseUUID(userId);
        log.debug("Fetching Reservations for User ID: {}", userUUID);

        List<Reservation> reservations = reservationRepository.findAllByUser_Id(userUUID);
        List<ReservationDto> reservationDtos = reservations.stream()
                .map(reservationMapper::toDto)
                .toList();

        log.info("Fetched {} reservations for User ID: {}", reservationDtos.size(), userId);
        return reservationDtos;
    }

    @Override
    public List<ReservationDto> getReservationsByTableId(String tableId) {
        UUID tableUUID = UUIDUtils.parseUUID(tableId);
        log.debug("Fetching Reservations for Table ID: {}", tableUUID);

        List<Reservation> reservations = reservationRepository.findAllByTable_Id(tableUUID);
        List<ReservationDto> reservationDtos = reservations.stream()
                .map(reservationMapper::toDto)
                .toList();

        log.info("Fetched {} reservations for Table ID: {}", reservationDtos.size(), tableId);
        return reservationDtos;
    }

    @Override
    public List<ReservationDto> getReservationsByGuestName(String guestName) {
        log.debug("Fetching Reservations for Guest Name: {}", guestName);

        List<Reservation> reservations = reservationRepository.findAllByGuestName(guestName);
        List<ReservationDto> reservationDtos = reservations.stream()
                .map(reservationMapper::toDto)
                .toList();

        log.info("Fetched {} reservations for Guest Name: {}", reservationDtos.size(), guestName);
        return reservationDtos;
    }

    @Override
    public List<ReservationDto> getReservationsByGuestEmail(String guestEmail) {
        log.debug("Fetching Reservations for Guest Email: {}", guestEmail);

        List<Reservation> reservations = reservationRepository.findAllByGuestEmail(guestEmail);
        List<ReservationDto> reservationDtos = reservations.stream()
                .map(reservationMapper::toDto)
                .toList();

        log.info("Fetched {} reservations for Guest Email: {}", reservationDtos.size(), guestEmail);
        return reservationDtos;
    }

    @Override
    public List<ReservationDto> getReservationsByGuestPhone(String guestPhone) {
        log.debug("Fetching Reservations for Guest Phone: {}", guestPhone);

        List<Reservation> reservations = reservationRepository.findAllByGuestPhone(guestPhone);
        List<ReservationDto> reservationDtos = reservations.stream()
                .map(reservationMapper::toDto)
                .toList();

        log.info("Fetched {} reservations for Guest Phone: {}", reservationDtos.size(), guestPhone);
        return reservationDtos;
    }

    @Override
    public ReservationDto createReservation(ReservationCreateDto reservationCreateDto) {
        log.info("Attempting to create reservation with data: {}", reservationCreateDto);

        String validationError = validateReservationData(reservationCreateDto.getReservationTime());
        if (validationError != null) {
            throw new BadRequestException(validationError);
        }

        UUID tableId = UUIDUtils.parseUUID(reservationCreateDto.getTableId());
        TableEntity table = tableRepository.findById(tableId)
                .orElseThrow(() -> {
                    log.warn("Table not found with ID: {}", reservationCreateDto.getTableId());
                    return new ResourceNotFoundException("Table not found with ID: " + reservationCreateDto.getTableId());
                });

        User user = null;
        if (reservationCreateDto.getUserId() != null && !reservationCreateDto.getUserId().isEmpty()) {
            UUID userId = UUIDUtils.parseUUID(reservationCreateDto.getUserId());
            user = userRepository.findById(userId)
                    .orElseThrow(() -> {
                        log.warn("User not found with ID: {}", reservationCreateDto.getUserId());
                        return new ResourceNotFoundException("User not found with ID: " + reservationCreateDto.getUserId());
                    });
        }

        if (reservationCreateDto.getNumberOfGuests() > table.getCapacity()) {
            log.warn("Number of guests {} exceeds table capacity {}.", reservationCreateDto.getNumberOfGuests(), table.getCapacity());
            throw new BadRequestException("Number of guests exceeds table capacity.");
        }

        Reservation reservation = setReservation(reservationCreateDto, table, user);

        if (isOverlappingReservation(reservation)) {
            log.warn("Table {} is already reserved at {}", tableId, reservation.getReservationTime());
            throw new ConflictException("Table is already reserved at the requested time.");
        }

        if (user != null) {
            if (hasUserExistingReservation(reservation)) {
                log.warn("User {} already has a reservation at {}", user.getId(), reservation.getReservationTime());
                throw new ConflictException("User already has a reservation at the requested time.");
            }
        } else {
            if (hasGuestExistingReservation(reservation)) {
                log.warn("Guest with email {} already has a reservation at {}", reservation.getGuestEmail(), reservation.getReservationTime());
                throw new ConflictException("Guest with this email already has a reservation at the requested time.");
            }
        }

        if (user != null) {
            user.addReservation(reservation);
        }
        table.addReservation(reservation);

        Reservation savedReservation = reservationRepository.save(reservation);
        entityManager.flush();
        entityManager.refresh(savedReservation);
        log.info("Reservation created successfully with ID: {}", savedReservation.getId());

        return reservationMapper.toDto(savedReservation);
    }

    /**
     * Creates a new reservation.
     *
     * @param reservationCreateDto The DTO containing reservation creation data.
     * @param table The table entity.
     * @param user The user entity.
     * @return ReservationDto
     */
    private static Reservation setReservation(ReservationCreateDto reservationCreateDto, TableEntity table, User user) {
        Reservation reservation = new Reservation();
        reservation.setReservationTime(reservationCreateDto.getReservationTime());
        reservation.setNumberOfGuests(reservationCreateDto.getNumberOfGuests());
        reservation.setTable(table);

        if (user != null) {
            reservation.setUser(user);
            reservation.setGuestPhone(null);
            reservation.setGuestEmail(null);
            reservation.setGuestName(null);
        } else {
            reservation.setGuestPhone(reservationCreateDto.getGuestPhone());
            reservation.setGuestEmail(reservationCreateDto.getGuestEmail());
            reservation.setGuestName(reservationCreateDto.getGuestName());
        }
        return reservation;
    }


    @Override
    public String deleteReservation(String reservationId) {
        UUID uuid = UUIDUtils.parseUUID(reservationId);
        log.debug("Attempting to delete Reservation with ID: {}", uuid);

        Reservation reservation = reservationRepository.findById(uuid)
                .orElseThrow(() -> {
                    log.warn("Reservation not found for deletion with ID: {}", reservationId);
                    return new ResourceNotFoundException("Reservation not found with ID: " + reservationId);
                });

        if (reservation.getDeleted()) {
            log.warn("Reservation ID {} is already deleted.", reservationId);
            throw new BadRequestException("Reservation is already deleted.");
        }

        reservation.setDeleted(true);

        if (reservation.getUser() != null)
            reservation.getUser().removeReservation(reservation);

        if (reservation.getTable() != null)
            reservation.getTable().removeReservation(reservation);


        reservationRepository.save(reservation);
        log.info("Reservation soft-deleted successfully with ID: {}", reservationId);
        return "Reservation deleted successfully.";
    }

    @Override
    public ReservationDto updateReservation(String reservationId, ReservationCreateDto reservationCreateDto) {
        log.info("Attempting to update reservation with ID: {} using data: {}", reservationId, reservationCreateDto);

        UUID uuid = UUIDUtils.parseUUID(reservationId);

        Reservation existingReservation = reservationRepository.findById(uuid)
                .orElseThrow(() -> {
                    log.warn("Reservation not found for update with ID: {}", reservationId);
                    return new ResourceNotFoundException("Reservation not found with ID: " + reservationId);
                });

        if (existingReservation.getDeleted()) {
            log.warn("Attempt to update deleted Reservation ID: {}", reservationId);
            throw new BadRequestException("Cannot update a deleted reservation.");
        }

        String validationError = validateReservationData(reservationCreateDto.getReservationTime());
        if (validationError != null) {
            throw new BadRequestException(validationError);
        }

        TableEntity newTable = existingReservation.getTable();
        if (reservationCreateDto.getTableId() != null && !reservationCreateDto.getTableId().isEmpty()) {
            UUID newTableId = UUIDUtils.parseUUID(reservationCreateDto.getTableId());
            if (!existingReservation.getTable().getId().equals(newTableId)) {
                newTable = tableRepository.findById(newTableId)
                        .orElseThrow(() -> {
                            log.warn("Table not found with ID: {}", reservationCreateDto.getTableId());
                            return new ResourceNotFoundException("Table not found with ID: " + reservationCreateDto.getTableId());
                        });
            }
        }

        User newUser = existingReservation.getUser();
        if (reservationCreateDto.getUserId() != null && !reservationCreateDto.getUserId().isEmpty()) {
            UUID newUserId = UUIDUtils.parseUUID(reservationCreateDto.getUserId());
            if (existingReservation.getUser() == null || !existingReservation.getUser().getId().equals(newUserId)) {
                newUser = userRepository.findById(newUserId)
                        .orElseThrow(() -> {
                            log.warn("User not found with ID: {}", reservationCreateDto.getUserId());
                            return new ResourceNotFoundException("User not found with ID: " + reservationCreateDto.getUserId());
                        });
            }
        } else {
            newUser = null;
        }

        if (reservationCreateDto.getNumberOfGuests() > newTable.getCapacity()) {
            log.warn("Number of guests {} exceeds table capacity {}.", reservationCreateDto.getNumberOfGuests(), newTable.getCapacity());
            throw new BadRequestException("Number of guests exceeds table capacity.");
        }

        Reservation tempReservation = new Reservation();
        tempReservation.setReservationTime(reservationCreateDto.getReservationTime());
        tempReservation.setTable(newTable);
        tempReservation.setId(uuid);

        if (isOverlappingReservation(tempReservation, uuid)) {
            log.warn("Table {} is already reserved at {}", newTable.getId(), reservationCreateDto.getReservationTime());
            throw new ConflictException("Table is already reserved at the requested time.");
        }

        if (newUser != null) {
            if (hasUserExistingReservation(reservationCreateDto.getReservationTime(), newUser.getId(), uuid)) {
                log.warn("User {} already has a reservation at {}", newUser.getId(), reservationCreateDto.getReservationTime());
                throw new ConflictException("User already has a reservation at the requested time.");
            }
        } else {
            if (hasGuestExistingReservation(reservationCreateDto.getReservationTime(), reservationCreateDto.getGuestEmail(), uuid)) {
                log.warn("Guest with email {} already has a reservation at {}", reservationCreateDto.getGuestEmail(), reservationCreateDto.getReservationTime());
                throw new ConflictException("Guest with this email already has a reservation at the requested time.");
            }
        }

        if (newUser != null) {
            if (existingReservation.getUser() != null && !existingReservation.getUser().getId().equals(newUser.getId())) {
                existingReservation.getUser().removeReservation(existingReservation);
            }
            existingReservation.setUser(newUser);
            newUser.addReservation(existingReservation);
            existingReservation.setGuestName(null);
            existingReservation.setGuestEmail(null);
            existingReservation.setGuestPhone(null);
        } else {
            if (existingReservation.getUser() != null) {
                existingReservation.getUser().removeReservation(existingReservation);
                existingReservation.setUser(null);
            }
            existingReservation.setGuestName(reservationCreateDto.getGuestName());
            existingReservation.setGuestEmail(reservationCreateDto.getGuestEmail());
            existingReservation.setGuestPhone(reservationCreateDto.getGuestPhone());
        }

        if (!existingReservation.getTable().getId().equals(newTable.getId())) {
            existingReservation.getTable().removeReservation(existingReservation);
            existingReservation.setTable(newTable);
            newTable.addReservation(existingReservation);
        }

        existingReservation.setReservationTime(reservationCreateDto.getReservationTime());
        existingReservation.setNumberOfGuests(reservationCreateDto.getNumberOfGuests());

        Reservation updatedReservation = reservationRepository.save(existingReservation);
        log.info("Reservation updated successfully with ID: {}", reservationId);

        return reservationMapper.toDto(updatedReservation);
    }

    // ------------------- Private Validation Methods -------------------

    /**
     * Validates the reservation data.
     *
     * @param reservationTime The desired reservation time.
     * @return Error message if validation fails, otherwise null.
     */
    private String validateReservationData(LocalDateTime reservationTime) {
        LocalDate reservationDate = reservationTime.toLocalDate();
        LocalDate today = LocalDate.now();

        if (!reservationDate.isAfter(today)) {
            return "Reservation can't be made for today or past dates.";
        }

        LocalTime reservationStart = reservationTime.toLocalTime();
        LocalTime reservationEnd = reservationStart.plus(RESERVATION_DURATION).plusMinutes(BUFFER_DURATION.toMinutes());

        if (reservationStart.isBefore(OPENING_TIME) || reservationEnd.isAfter(CLOSING_TIME)) {
            return "Reservation time is outside business hours.";
        }

        return null;
    }

    /**
     * Checks if the reservation overlaps with existing reservations.
     *
     * @param reservation The reservation to check.
     * @return True if overlapping exists, otherwise false.
     */
    private boolean isOverlappingReservation(Reservation reservation) {
        UUID tableId = reservation.getTable().getId();
        LocalDateTime requestedStart = reservation.getReservationTime();
        LocalDateTime requestedEnd = requestedStart.plus(RESERVATION_DURATION).plus(BUFFER_DURATION);

        // Check for overlapping reservations within the reservation duration and buffer
        return reservationRepository.existsByTable_IdAndReservationTimeBetween(
                tableId,
                requestedStart.minus(RESERVATION_DURATION).minus(BUFFER_DURATION),
                requestedEnd
        );
    }

    /**
     * Checks if the reservation overlaps with existing reservations, excluding a specific reservation ID.
     *
     * @param reservation The reservation to check.
     * @param excludeResId The reservation ID to exclude from the check.
     * @return True if overlapping exists, otherwise false.
     */
    private boolean isOverlappingReservation(Reservation reservation, UUID excludeResId) {
        UUID tableId = reservation.getTable().getId();
        LocalDateTime requestedStart = reservation.getReservationTime();
        LocalDateTime requestedEnd = requestedStart.plus(RESERVATION_DURATION).plus(BUFFER_DURATION);

        // Check for overlapping reservations within the reservation duration and buffer, excluding current reservation
        return reservationRepository.existsByTable_IdAndReservationTimeBetweenAndIdNot(
                tableId,
                requestedStart.minus(RESERVATION_DURATION).minus(BUFFER_DURATION),
                requestedEnd,
                excludeResId
        );
    }

    /**
     * Checks if the user already has a reservation at the specified time.
     *
     * @param reservationTime The desired reservation time.
     * @param userId          The UUID of the user.
     * @param excludeResId    The reservation ID to exclude from the check.
     * @return True if the user has an existing reservation at the time, otherwise false.
     */
    private boolean hasUserExistingReservation(LocalDateTime reservationTime, UUID userId, UUID excludeResId) {
        return reservationRepository.existsByUser_IdAndReservationTimeAndIdNot(userId, reservationTime, excludeResId);
    }

    /**
     * Checks if the user already has a reservation at the specified time.
     *
     * @param reservation The reservation entity.
     * @return True if the user has an existing reservation at the time, otherwise false.
     */
    private boolean hasUserExistingReservation(Reservation reservation) {
        UUID userId = reservation.getUser().getId();
        LocalDateTime reservationTime = reservation.getReservationTime();
        return reservationRepository.existsByUser_IdAndReservationTime(userId, reservationTime);
    }

    /**
     * Checks if the guest already has a reservation at the specified time.
     *
     * @param reservationTime The desired reservation time.
     * @param guestEmail      The email of the guest.
     * @param excludeResId    The reservation ID to exclude from the check.
     * @return True if the guest has an existing reservation at the time, otherwise false.
     */
    private boolean hasGuestExistingReservation(LocalDateTime reservationTime, String guestEmail, UUID excludeResId) {
        return reservationRepository.existsByGuestEmailAndReservationTimeAndIdNot(guestEmail, reservationTime, excludeResId);
    }

    /**
     * Checks if the guest already has a reservation at the specified time.
     *
     * @param reservation The reservation entity.
     * @return True if the guest has an existing reservation at the time, otherwise false.
     */
    private boolean hasGuestExistingReservation(Reservation reservation) {
        String guestEmail = reservation.getGuestEmail();
        LocalDateTime reservationTime = reservation.getReservationTime();
        return reservationRepository.existsByGuestEmailAndReservationTime(guestEmail, reservationTime);
    }
}
