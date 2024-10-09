package com.mitar.dipl.mapper;

import com.mitar.dipl.model.dto.reservation.ReservationCreateDto;
import com.mitar.dipl.model.dto.reservation.ReservationDto;
import com.mitar.dipl.model.entity.Reservation;
import com.mitar.dipl.model.entity.TableEntity;
import com.mitar.dipl.model.entity.User;
import com.mitar.dipl.repository.TableRepository;
import com.mitar.dipl.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@AllArgsConstructor
public class ReservationMapper {

    private final UserRepository userRepository;
    private final TableRepository tableRepository;

    public ReservationDto toDto(Reservation reservation) {
        ReservationDto dto = new ReservationDto();
        dto.setId(reservation.getId().toString());
        dto.setReservationTime(reservation.getReservationTime());
        dto.setNumberOfGuests(reservation.getNumberOfGuests());
        dto.setUserId(reservation.getUser().getId().toString());
        dto.setTableId(reservation.getTable().getId().toString());
        dto.setGuestName(reservation.getGuestName());
        dto.setGuestEmail(reservation.getGuestEmail());
        dto.setGuestPhone(reservation.getGuestPhone());
        return dto;
    }

    public Reservation toEntity(ReservationCreateDto dto) {
        Reservation reservation = new Reservation();
        reservation.setReservationTime(dto.getReservationTime());
        reservation.setNumberOfGuests(dto.getNumberOfGuests());
        reservation.setGuestName(dto.getGuestName());
        reservation.setGuestEmail(dto.getGuestEmail());
        reservation.setGuestPhone(dto.getGuestPhone());

        Optional<User> user = userRepository.findById(UUID.fromString(dto.getUserId()));
        user.ifPresent(reservation::setUser);

        Optional<TableEntity> table = tableRepository.findById(UUID.fromString(dto.getTableId()));
        table.ifPresent(reservation::setTable);

        return reservation;
    }

    public void updateEntityFromDto(ReservationCreateDto reservationCreateDto, Reservation existingReservation) {
        existingReservation.setReservationTime(reservationCreateDto.getReservationTime());
        existingReservation.setNumberOfGuests(reservationCreateDto.getNumberOfGuests());
        existingReservation.setGuestName(reservationCreateDto.getGuestName());
        existingReservation.setGuestEmail(reservationCreateDto.getGuestEmail());
        existingReservation.setGuestPhone(reservationCreateDto.getGuestPhone());

        Optional<User> user = userRepository.findById(UUID.fromString(reservationCreateDto.getUserId()));
        user.ifPresent(existingReservation::setUser);

        Optional<TableEntity> table = tableRepository.findById(UUID.fromString(reservationCreateDto.getTableId()));
        table.ifPresent(existingReservation::setTable);
    }
}
