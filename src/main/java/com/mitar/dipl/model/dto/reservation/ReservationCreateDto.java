package com.mitar.dipl.model.dto.reservation;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@ValidReservation
public class ReservationCreateDto {

    @NotNull(message = "Table ID is required.")
    @NotEmpty(message = "Table ID cannot be empty.")
    private String tableId;

    @NotNull(message = "Reservation time is required.")
    @Future(message = "Reservation time must be in the future.")
    private LocalDateTime reservationTime;

    @NotNull(message = "Number of guests is required.")
    @Min(value = 1, message = "There must be at least one guest.")
    private Integer numberOfGuests;

    private String userId;

    private String guestName;

    @Email(message = "Invalid guest email format.")
    private String guestEmail;

    @Pattern(regexp = "^\\+?[0-9]{7,15}$", message = "Invalid guest phone number format.")
    private String guestPhone;
}