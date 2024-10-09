package com.mitar.dipl.controller;

import com.mitar.dipl.model.dto.reservation.ReservationCreateDto;
import com.mitar.dipl.service.ReservationService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    @GetMapping("/all")
    public ResponseEntity<?> getAllReservations() {
        return reservationService.getAllReservations();
    }

    @GetMapping("/{reservationId}")
    public ResponseEntity<?> getReservationById(@PathVariable String reservationId) {
        return reservationService.getReservationById(reservationId);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getReservationsByUserId(@PathVariable String userId) {
        return reservationService.getReservationsByUserId(userId);
    }

    @GetMapping("/table/{tableId}")
    public ResponseEntity<?> getReservationsByTableId(@PathVariable String tableId) {
        return reservationService.getReservationsByTableId(tableId);
    }

    @GetMapping("/guest-name/{guestName}")
    public ResponseEntity<?> getReservationsByGuestName(@PathVariable String guestName) {
        return reservationService.getReservationsByGuestName(guestName);
    }

    @GetMapping("/guest-email/{guestEmail}")
    public ResponseEntity<?> getReservationsByGuestEmail(@PathVariable String guestEmail) {
        return reservationService.getReservationsByGuestEmail(guestEmail);
    }

    @GetMapping("/guest-phone/{guestPhone}")
    public ResponseEntity<?> getReservationsByGuestPhone(@PathVariable String guestPhone) {
        return reservationService.getReservationsByGuestPhone(guestPhone);
    }

    @PostMapping
    public ResponseEntity<?> createReservation(@RequestBody @Validated ReservationCreateDto reservationCreateDto) {
        return reservationService.createReservation(reservationCreateDto);
    }

    @DeleteMapping("/delete/{reservationId}")
    public ResponseEntity<?> deleteReservation(@PathVariable String reservationId) {
        return reservationService.deleteReservation(reservationId);
    }

    @PutMapping("/update/{reservationId}")
    public ResponseEntity<?> updateReservation(@PathVariable String reservationId, @RequestBody @Validated ReservationCreateDto reservationCreateDto) {
        return reservationService.updateReservation(reservationId, reservationCreateDto);
    }
}
