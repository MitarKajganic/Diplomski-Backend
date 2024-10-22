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

    public ReservationDto toDto(Reservation reservation) {
        ReservationDto dto = new ReservationDto();
        dto.setId(reservation.getId().toString());
        dto.setReservationTime(reservation.getReservationTime());
        dto.setNumberOfGuests(reservation.getNumberOfGuests());
        dto.setUserId(reservation.getUser() != null ? reservation.getUser().getId().toString() : null);
        dto.setTableId(reservation.getTable().getId().toString());
        dto.setGuestName(reservation.getGuestName() != null ? reservation.getGuestName() : null);
        dto.setGuestEmail(reservation.getGuestEmail() != null ? reservation.getGuestEmail() : null);
        dto.setGuestPhone(reservation.getGuestPhone() != null ? reservation.getGuestPhone() : null);
        return dto;
    }

}
