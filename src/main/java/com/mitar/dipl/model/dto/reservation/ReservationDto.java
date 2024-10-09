package com.mitar.dipl.model.dto.reservation;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReservationDto {

    private String id;
    private LocalDateTime reservationTime;
    private Integer numberOfGuests;
    private String userId;
    private String tableId;
    private String guestName;
    private String guestEmail;
    private String guestPhone;

}
