package com.mitar.dipl.service.implementation;

import com.mitar.dipl.exception.custom.BadRequestException;
import com.mitar.dipl.exception.custom.ConflictException;
import com.mitar.dipl.exception.custom.ResourceNotFoundException;
import com.mitar.dipl.mapper.ReservationMapper;
import com.mitar.dipl.model.dto.reservation.ReservationCreateDto;
import com.mitar.dipl.model.entity.Reservation;
import com.mitar.dipl.model.entity.TableEntity;
import com.mitar.dipl.model.entity.User;
import com.mitar.dipl.repository.ReservationRepository;
import com.mitar.dipl.repository.TableRepository;
import com.mitar.dipl.repository.UserRepository;
import com.mitar.dipl.service.ReservationService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;
import java.util.UUID;

import static com.mitar.dipl.service.implementation.UUIDUtils.parseUUID;

@Service
@AllArgsConstructor
@Transactional
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final TableRepository tableRepository;
    private final ReservationMapper reservationMapper;
    private static final Logger logger = LoggerFactory.getLogger(ReservationService.class);

    // Business hours constants
    private static final LocalTime OPENING_TIME = LocalTime.of(10, 0);
    private static final LocalTime CLOSING_TIME = LocalTime.of(22, 0);
    private static final Duration BUFFER_DURATION = Duration.ofMinutes(30);
    private static final Duration RESERVATION_DURATION = Duration.ofHours(2);

    @Override
    public ResponseEntity<?> getAllReservations() {
        logger.info("Fetching all reservations.");
        return ResponseEntity.status(HttpStatus.OK).body(reservationRepository.findAll().stream()
                .map(reservationMapper::toDto)
                .toList()
        );
    }

    @Override
    public ResponseEntity<?> getAllIncludingDeleted() {
        logger.info("Fetching all reservations including deleted.");
        return ResponseEntity.status(HttpStatus.OK).body(reservationRepository.findAllIncludingDeleted().stream()
                .map(reservationMapper::toDto)
                .toList()
        );
    }

    @Override
    public ResponseEntity<?> getReservationById(String reservationId) {
        UUID reservationUUID = parseUUID(reservationId);
        Reservation reservation = reservationRepository.findById(reservationUUID)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found."));
        return ResponseEntity.status(HttpStatus.OK).body(reservationMapper.toDto(reservation));
    }

    @Override
    public ResponseEntity<?> getReservationsByUserId(String userId) {
        UUID userUUID = parseUUID(userId);
        return ResponseEntity.status(HttpStatus.OK).body(reservationRepository.findAllByUser_Id(userUUID).stream()
                .map(reservationMapper::toDto)
                .toList()
        );
    }

    @Override
    public ResponseEntity<?> getReservationsByTableId(String tableId) {
        UUID tableUUID = parseUUID(tableId);
        return ResponseEntity.status(HttpStatus.OK).body(reservationRepository.findAllByTable_Id(tableUUID).stream()
                .map(reservationMapper::toDto)
                .toList()
        );
    }

    @Override
    public ResponseEntity<?> getReservationsByGuestName(String guestName) {
        return ResponseEntity.status(HttpStatus.OK).body(reservationRepository.findAllByGuestName(guestName).stream()
                .map(reservationMapper::toDto)
                .toList()
        );
    }

    @Override
    public ResponseEntity<?> getReservationsByGuestEmail(String guestEmail) {
        return ResponseEntity.status(HttpStatus.OK).body(reservationRepository.findAllByGuestEmail(guestEmail).stream()
                .map(reservationMapper::toDto)
                .toList()
        );
    }

    @Override
    public ResponseEntity<?> getReservationsByGuestPhone(String guestPhone) {
        return ResponseEntity.status(HttpStatus.OK).body(reservationRepository.findAllByGuestPhone(guestPhone).stream()
                .map(reservationMapper::toDto)
                .toList()
        );
    }

    @Override
    public ResponseEntity<?> createReservation(ReservationCreateDto reservationCreateDto) {
        logger.info("Attempting to create reservation with data: {}", reservationCreateDto);

        String validation = validateReservationData(reservationCreateDto.getReservationTime());

        if (validation != null) {
            throw new BadRequestException(validation);
        }

        Reservation reservation = new Reservation();
        reservation.setReservationTime(reservationCreateDto.getReservationTime());

        UUID tableId = parseUUID(reservationCreateDto.getTableId());
        TableEntity table = tableRepository.findById(tableId)
                .orElseThrow(() -> new ResourceNotFoundException("Table not found."));

        User user = null;
        if (reservationCreateDto.getUserId() != null) {
            UUID userId = parseUUID(reservationCreateDto.getUserId());
            user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found."));
        }

        if (reservationCreateDto.getNumberOfGuests() > table.getCapacity())
            throw new BadRequestException("Number of guests exceeds table capacity.");

        reservation.setNumberOfGuests(reservationCreateDto.getNumberOfGuests());

        if (user != null) {
            reservation.setUser(user);
            reservation.setGuestPhone(null);
            reservation.setGuestEmail(null);
            reservation.setGuestName(null);
        } else {
            reservation.setUser(null);
            reservation.setGuestPhone(reservationCreateDto.getGuestPhone());
            reservation.setGuestEmail(reservationCreateDto.getGuestEmail());
            reservation.setGuestName(reservationCreateDto.getGuestName());
        }

        reservation.setTable(table);

        if (isOverlappingReservation(reservation)) {
            throw new ConflictException("Table is already reserved at that time.");
        }

        if (user != null) {
            if (hasUserExistingReservation(reservation)) {
                throw new ConflictException("User already has a reservation at this time.");
            }
        } else {
            if (hasGuestExistingReservation(reservation)) {
                throw new ConflictException("Guest with this email already has a reservation at this time.");
            }
        }

        if (user != null) {
            user.addReservation(reservation);
        }
        table.addReservation(reservation);

        Reservation savedReservation = reservationRepository.save(reservation);
        logger.info("Reservation created successfully: {}", savedReservation.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(reservationMapper.toDto(savedReservation));
    }


    @Override
    public ResponseEntity<?> deleteReservation(String reservationId) {
        UUID uuid = parseUUID(reservationId);

        Reservation reservation = reservationRepository.findById(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found."));

        if (reservation.getDeleted()) {
            throw new BadRequestException("Reservation is already deleted.");
        }

        reservation.setDeleted(true);

        if (reservation.getUser() != null) {
            reservation.getUser().removeReservation(reservation);
        }
        if (reservation.getTable() != null) {
            reservation.getTable().removeReservation(reservation);
        }

        reservationRepository.save(reservation);
        logger.info("Reservation soft-deleted successfully: {}", reservationId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Reservation deleted successfully.");
    }

    @Override
    public ResponseEntity<?> updateReservation(String reservationId, ReservationCreateDto reservationCreateDto) {
        logger.info("Attempting to update reservation with ID: {} using data: {}", reservationId, reservationCreateDto);

        UUID uuid = parseUUID(reservationId);

        Reservation existingReservation = reservationRepository.findById(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found."));

        if (existingReservation.getDeleted())
            throw new BadRequestException("Reservation is already deleted.");

        String validation = validateReservationData(reservationCreateDto.getReservationTime());

        if (validation != null) {
            throw new BadRequestException(validation);
        }

        User newUser = null;
        if (reservationCreateDto.getUserId() != null) {
            UUID newUserId = parseUUID(reservationCreateDto.getUserId());
            if (existingReservation.getUser() == null || !existingReservation.getUser().getId().equals(newUserId)) {
                newUser = userRepository.findById(newUserId)
                        .orElseThrow(() -> new ResourceNotFoundException("User not found."));
            }
        }

        TableEntity newTable = null;
        if (reservationCreateDto.getTableId() != null) {
            UUID newTableId = parseUUID(reservationCreateDto.getTableId());
            if (!existingReservation.getTable().getId().equals(newTableId)) {
                newTable = tableRepository.findById(newTableId)
                        .orElseThrow(() -> new ResourceNotFoundException("Table not found."));
            }
        }

        Reservation tempReservation = new Reservation();
        tempReservation.setReservationTime(reservationCreateDto.getReservationTime());
        tempReservation.setNumberOfGuests(reservationCreateDto.getNumberOfGuests());

        tempReservation.setId(existingReservation.getId());
        tempReservation.setUser(newUser != null ? newUser : existingReservation.getUser());
        tempReservation.setTable(newTable != null ? newTable : existingReservation.getTable());

        if (tempReservation.getNumberOfGuests() > tempReservation.getTable().getCapacity())
            throw new BadRequestException("Number of guests exceeds table capacity.");


        if (isOverlappingReservation(tempReservation, uuid)) {
            throw new ConflictException("Table is already reserved at that time.");
        }

        if (newUser != null) {
            if (hasUserExistingReservation(tempReservation, uuid)) {
                throw new ConflictException("User already has a reservation at this time.");
            }
        } else if (reservationCreateDto.getUserId() == null && existingReservation.getUser() != null) {
            if (hasGuestExistingReservation(tempReservation, uuid)) {
                throw new ConflictException("Guest with this email already has a reservation at this time.");
            }
        }

        if (newUser != null) {
            if (existingReservation.getUser() != null) {
                existingReservation.getUser().removeReservation(existingReservation);
            }
            existingReservation.setUser(newUser);
            newUser.addReservation(existingReservation);
        } else if (reservationCreateDto.getUserId() == null && existingReservation.getUser() != null) {
            existingReservation.getUser().removeReservation(existingReservation);
            existingReservation.setUser(null);
        }

        if (newTable != null) {
            existingReservation.getTable().removeReservation(existingReservation);
            existingReservation.setTable(newTable);
            newTable.addReservation(existingReservation);
        }

        existingReservation.setReservationTime(reservationCreateDto.getReservationTime());
        existingReservation.setNumberOfGuests(reservationCreateDto.getNumberOfGuests());

        Reservation updatedReservation = reservationRepository.save(existingReservation);
        logger.info("Reservation updated successfully: {}", updatedReservation.getId());

        return ResponseEntity.status(HttpStatus.OK).body(reservationMapper.toDto(updatedReservation));
    }


    // ------------------- Private Validation Methods -------------------

    /**
     * Validates the reservation data.
     *
     * @param reservationTime the reservation entity
     * @return error message if validation fails, otherwise null
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
     * @param reservation the reservation to check
     * @return true if overlapping exists, otherwise false
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
     * @param reservation the reservation to check
     * @param excludeResId the reservation ID to exclude from the check
     * @return true if overlapping exists, otherwise false
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
     * @param reservation the reservation entity
     * @return true if the user has an existing reservation at the time, otherwise false
     */
    private boolean hasUserExistingReservation(Reservation reservation) {
        UUID userId = reservation.getUser().getId();
        LocalDateTime reservationTime = reservation.getReservationTime();
        return reservationRepository.existsByUser_IdAndReservationTime(userId, reservationTime);
    }

    /**
     * Checks if the user already has a reservation at the specified time, excluding a specific reservation ID.
     *
     * @param reservation the reservation entity
     * @param excludeResId the reservation ID to exclude from the check
     * @return true if the user has an existing reservation at the time, otherwise false
     */
    private boolean hasUserExistingReservation(Reservation reservation, UUID excludeResId) {
        UUID userId = reservation.getUser().getId();
        LocalDateTime reservationTime = reservation.getReservationTime();
        return reservationRepository.existsByUser_IdAndReservationTimeAndIdNot(userId, reservationTime, excludeResId);
    }

    /**
     * Checks if the guest already has a reservation at the specified time.
     *
     * @param reservation the reservation entity
     * @return true if the guest has an existing reservation at the time, otherwise false
     */
    private boolean hasGuestExistingReservation(Reservation reservation) {
        String guestEmail = reservation.getGuestEmail();
        LocalDateTime reservationTime = reservation.getReservationTime();
        return reservationRepository.existsByGuestEmailAndReservationTime(guestEmail, reservationTime);
    }

    /**
     * Checks if the guest already has a reservation at the specified time, excluding a specific reservation ID.
     *
     * @param reservation the reservation entity
     * @param excludeResId the reservation ID to exclude from the check
     * @return true if the guest has an existing reservation at the time, otherwise false
     */
    private boolean hasGuestExistingReservation(Reservation reservation, UUID excludeResId) {
        String guestEmail = reservation.getGuestEmail();
        LocalDateTime reservationTime = reservation.getReservationTime();
        return reservationRepository.existsByGuestEmailAndReservationTimeAndIdNot(guestEmail, reservationTime, excludeResId);
    }

}
