package com.mitar.dipl.service;

import com.mitar.dipl.model.dto.reservation.ReservationCreateDto;
import com.mitar.dipl.model.dto.reservation.ReservationDto;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ReservationService {

    /**
     * Fetches all reservations.
     *
     * @return List of ReservationDto
     */
    List<ReservationDto> getAllReservations();

    /**
     * Fetches all reservations including soft-deleted ones.
     *
     * @return List of ReservationDto
     */
    List<ReservationDto> getAllIncludingDeleted();

    /**
     * Fetches a reservation by its ID.
     *
     * @param reservationId The UUID of the reservation as a string.
     * @return ReservationDto
     */
    ReservationDto getReservationById(String reservationId);

    /**
     * Fetches reservations by User ID.
     *
     * @param userId The UUID of the user as a string.
     * @return List of ReservationDto
     */
    List<ReservationDto> getReservationsByUserId(String userId);

    /**
     * Fetches reservations by Table ID.
     *
     * @param tableId The UUID of the table as a string.
     * @return List of ReservationDto
     */
    List<ReservationDto> getReservationsByTableId(String tableId);

    /**
     * Fetches reservations by Guest Name.
     *
     * @param guestName The name of the guest.
     * @return List of ReservationDto
     */
    List<ReservationDto> getReservationsByGuestName(String guestName);

    /**
     * Fetches reservations by Guest Email.
     *
     * @param guestEmail The email of the guest.
     * @return List of ReservationDto
     */
    List<ReservationDto> getReservationsByGuestEmail(String guestEmail);

    /**
     * Fetches reservations by Guest Phone.
     *
     * @param guestPhone The phone number of the guest.
     * @return List of ReservationDto
     */
    List<ReservationDto> getReservationsByGuestPhone(String guestPhone);

    /**
     * Creates a new reservation.
     *
     * @param reservationCreateDto The DTO containing reservation creation data.
     * @return ReservationDto
     */
    ReservationDto createReservation(ReservationCreateDto reservationCreateDto);

    /**
     * Deletes (soft-deletes) a reservation by its ID.
     *
     * @param reservationId The UUID of the reservation as a string.
     * @return Success message.
     */
    String deleteReservation(String reservationId);

    /**
     * Updates an existing reservation.
     *
     * @param reservationId        The UUID of the reservation as a string.
     * @param reservationCreateDto The DTO containing updated reservation data.
     * @return ReservationDto
     */
    ReservationDto updateReservation(String reservationId, ReservationCreateDto reservationCreateDto);

}
