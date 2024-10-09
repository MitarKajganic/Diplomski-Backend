package com.mitar.dipl.service;

import com.mitar.dipl.model.dto.reservation.ReservationCreateDto;
import org.springframework.http.ResponseEntity;

public interface ReservationService {

    ResponseEntity<?> getAllReservations();

    ResponseEntity<?> getReservationById(String reservationId);

    ResponseEntity<?> getReservationsByUserId(String userId);

    ResponseEntity<?> getReservationsByTableId(String tableId);

    ResponseEntity<?> getReservationsByGuestName(String guestName);

    ResponseEntity<?> getReservationsByGuestEmail(String guestEmail);

    ResponseEntity<?> getReservationsByGuestPhone(String guestPhone);

    ResponseEntity<?> createReservation(ReservationCreateDto reservationCreateDto);

    ResponseEntity<?> deleteReservation(String reservationId);

    ResponseEntity<?> updateReservation(String reservationId, ReservationCreateDto reservationCreateDto);

}
