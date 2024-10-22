package com.mitar.dipl.service;

import com.mitar.dipl.model.dto.reservation.ReservationCreateDto;
import com.mitar.dipl.model.dto.reservation.ReservationDto;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ReservationService {

    List<ReservationDto> getAllReservations();

    List<ReservationDto> getAllIncludingDeleted();

    ReservationDto getReservationById(String reservationId);

    List<ReservationDto> getReservationsByUserId(String userId);

    List<ReservationDto> getReservationsByTableId(String tableId);

    List<ReservationDto> getReservationsByGuestName(String guestName);

    List<ReservationDto> getReservationsByGuestEmail(String guestEmail);

    List<ReservationDto> getReservationsByGuestPhone(String guestPhone);

    ReservationDto createReservation(ReservationCreateDto reservationCreateDto);

    String deleteReservation(String reservationId);

    ReservationDto updateReservation(String reservationId, ReservationCreateDto reservationCreateDto);

}
