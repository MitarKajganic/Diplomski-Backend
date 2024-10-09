package com.mitar.dipl.model.dto.table_entity;

import com.mitar.dipl.model.dto.reservation.ReservationDto;
import lombok.Data;

import java.util.Set;

@Data
public class TableDto {

    private String id;
    private Integer tableNumber;
    private Integer capacity;
    private Boolean isAvailable;
    private Set<ReservationDto> reservations;
}
