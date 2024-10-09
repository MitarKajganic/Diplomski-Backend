package com.mitar.dipl.model.dto.reservation;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReservationCreateDto {

    @NotNull(message = "Table ID is required.")
    private String tableId;

    @NotNull(message = "Reservation time is required.")
    @Future(message = "Reservation time must be in the future.")
    private LocalDateTime reservationTime;

    @NotNull(message = "Number of guests is required.")
    @Min(value = 1, message = "There must be at least one guest.")
    private Integer numberOfGuests;

    // User ID if the reservation is made by a registered user
    private String userId;

    // Guest information if the reservation is made by a guest
    private String guestName;

    @Email(message = "Invalid guest email format.")
    private String guestEmail;

    @Pattern(regexp = "^\\+?[0-9]{7,15}$", message = "Invalid guest phone number format.")
    private String guestPhone;
}
